<?php

class LinkCheckAdmin extends ModelAdmin
{
    private static $managed_models = array('LinkCheckSite');
    private static $url_segment = 'linkcheck';
    private $menu_title = 'Link Check';
}