<?php

class LinkCheckRun extends DataObject
{

    private static $has_one = array(
        'LinkCheckSite' => 'LinkCheckSite',
        'LinkCheckFile' => 'File'
    );

    private static $summary_fields = array(
        'LinkCheckSite.SiteName' => 'Site Name',
        'LinkCheckFile.Title' => 'File Name',
        'Created' => 'Date Processed'
    );

    public function getCMSFields()
    {
        $fields = parent::getCMSFields();

        $fields->replaceField(
            'LinkCheckSiteID',
            $field = new ReadonlyField('Site', 'Site', $this->LinkCheckSite()->SiteName)
        );
        $fields->replaceField(
            'LinkCheckFile',
            $field = new LiteralField('File', "<a href=" . $this->LinkCheckFile()->Filename . ">Download File</a>")
        );

        return $fields;
    }

}