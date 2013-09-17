<?php

class LinkCheckEmail extends DataObject
{

    private static $db = array(
        'Name' => 'Varchar(255)',
        'Email' => 'Varchar(255)'
    );

    private static $belongs_many_many = array(
        'LinkCheckSites' => 'LinkCheckSite'
    );

    private static $summary_fields = array(
        'Name' => 'Name',
        'Email' => 'Email'
    );

}