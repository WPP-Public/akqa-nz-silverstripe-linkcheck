<?php

class LinkCheckSite extends DataObject
{

    private static $db = array(
        'SiteName' => 'Varchar(255)',
        'SiteURL' => 'Varchar(255)',
    );

    private static $has_many = array(
        'LinkCheckRuns' => 'LinkCheckRun'
    );

    private static $many_many = array(
        'EmailRecipients' => 'LinkCheckEmail'
    );

    private static $summary_fields = array(
        'SiteName' => 'Site Name',
        'SiteURL' => 'Site URL'
    );

    public function getCMSFields()
    {
        $fields = parent::getCMSFields();
        $fields->removeByName('EmailRecipients');
        $fields->removeByName('LinkCheckRuns');

        $fields->addFieldToTab(
            'Root.Main',
            $siteUrl = new TextField('SiteName', 'Site Name')
        );

        $fields->addFieldToTab(
            'Root.Main',
            $siteUrl = new TextField('SiteURL', 'Site URL')
        );

        $fields->addFieldToTab(
            'Root.Main',
            new GridField(
                "EmailRecipients",
                "Email Recipients",
                $this->EmailRecipients(),
                $emailRecipientsConfig = GridFieldConfig_RelationEditor::create()
            )
        );

        $fields->addFieldToTab(
            'Root.PreviousRuns',
            $emailRecipients = new GridField(
                'LinkCheckRuns',
                'Previous Link Check Runs',
                $this->LinkCheckRuns(),
                $config = GridFieldConfig_RelationEditor::create()
            )
        );

        return $fields;

    }

}