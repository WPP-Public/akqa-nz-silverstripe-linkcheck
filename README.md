# SilverStripe Link Check

Checks managed websites for broken links and produces a report which is emailed to interested parties in PDF format.

## License

SilverStripe Link Check is released under the [MIT license](http://heyday.mit-license.org/)

## Installation

	$ composer require silverstripe-linkcheck

## How to use

### Setting up checks

Set up a Link Check site in `/admin/linkcheck/` filling out the name and URL to check. Add email addresses to send the
results to.

### Running the task

Create a cronjob which runs frequently to perform checks. An example cronjob which runs weekly on Friday morning is
below.

```bash
0 9 * * 5 /path/to/webroot/framework/sake LinkCheckTask
```

## Contributing

### Code guidelines

This project follows the standards defined in:

* [PSR-0](https://github.com/php-fig/fig-standards/blob/master/accepted/PSR-0.md)
* [PSR-1](https://github.com/php-fig/fig-standards/blob/master/accepted/PSR-1-basic-coding-standard.md)