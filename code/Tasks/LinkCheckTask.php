<?php

use Heyday\SilverStripe\WkHtml;

class LinkCheckTask extends CliController
{

    private static $crawler = "crawler/Crawler.java";
    private static $linkStats = "crawler/LinkStats.java";
    private static $linkProject = "Project.java";
    private static $config;

    public function process()
    {
        self::$config = $this->config();

        if (!self::$config->wkHtmlToPdfPath) {
            throw new Exception("You must provide a path for WkHtmlToPdf in your sites configuration.");
        }

        if (!self::$config->emailAddress) {
            throw new Exception("You must provide an email address to send from in your sites configuration.");
        }

        increase_memory_limit_to('1024M');
        set_time_limit(0);

        $sites = LinkCheckSite::get();
        $outputDir = BASE_PATH . DIRECTORY_SEPARATOR . "silverstripe-linkcheck/runs/";
        $filesCreated = array();

        // build the crawler
        chdir(__DIR__ . "/../thirdparty");
        exec("javac " . self::$crawler . " " . self::$linkStats . " && " . "javac " . self::$linkProject);

        if ($sites) {

            foreach ($sites as $site) {

                echo "Checking " . $site->SiteURL . "\r\n";

                $url = $site->SiteURL;

                // if the output directory doesn't exist for the run, create it
                if (!file_exists($outputDir . str_replace("http://", "", $url))) {
                    mkdir($outputDir . str_replace("http://", "", $url));
                }

                $filename = date("Y-m-d") . '-' . rand(0, 1000) . ".html";
                $filepath = $outputDir . str_replace("http://", "", $url) . '/';

                // execute the crawler
                exec("java Project $url " . $filepath . $filename . " 10 1000");


                $filesCreated[$site->ID]['FilePath'] = $filepath;
                $filesCreated[$site->ID]['FileName'] = $filename;
                $filesCreated[$site->ID]['SiteName'] = $site->SiteName;
                $filesCreated[$site->ID]['ID'] = $site->ID;
                $filesCreated[$site->ID]['URL'] = $url;

                $emailRecipients = $site->EmailRecipients();

                if ($emailRecipients) {

                    foreach ($emailRecipients as $recipient) {

                        $filesCreated[$site->ID]['Email'][] = $recipient->Email;

                    }
                }
            }


            foreach ($filesCreated as $file) {

                Folder::find_or_make("LinkCheck" . DIRECTORY_SEPARATOR . $file['SiteName'] . DIRECTORY_SEPARATOR);
                $pdfPath = "assets" . DIRECTORY_SEPARATOR . "LinkCheck" . DIRECTORY_SEPARATOR . $file['SiteName'] . DIRECTORY_SEPARATOR;
                $pdfFullPath = BASE_PATH . DIRECTORY_SEPARATOR . $pdfPath;
                $pdfName = str_replace("html", "pdf", $file['FileName']);

                $generator = new WkHtml\Generator(
                    new \Knp\Snappy\Pdf(self::$config->wkHtmlToPdfPath),
                    new WkHtml\Input\String(file_get_contents($file['FilePath'] . $file['FileName'])),
                    new WkHtml\Output\File($pdfFullPath . $pdfName, 'application/pdf')
                );

                $generator->process();

                $site = LinkCheckSite::get()->byID($file['ID']);

                $pdfUpload = new File();
                $pdfUpload->Title = $file['SiteName'] . '-' . $pdfName;
                $pdfUpload->Filename = $pdfPath . $pdfName;
                $pdfUpload->write();

                $linkCheckRun = new LinkCheckRun();
                $linkCheckRun->LinkCheckFileID = $pdfUpload->ID;
                $linkCheckRun->LinkCheckSiteID = $site->ID;
                $linkCheckRun->write();

                $site->LinkCheckRuns()->add($linkCheckRun);

                foreach ($file['Email'] as $emailAddress) {
                    $email = new Email();
                    $email->to = $emailAddress;
                    $email->from = $this->config()->emailAddress;
                    $email->subject = $file['SiteName'] . " link check run";
                    $email->body = "Site Link Check Run for {$file['URL']} on " . date("Y/m/d");
                    $email->attachFile($pdfPath . $pdfName, "linkcheck.pdf");

                    $email->send();
                }

                unlink($file['FilePath'] . $file['FileName']);
            }

        }


    }


}
