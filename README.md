# Sonar Vcsparser Plugin

[![Build status (main)][build main badge]][build main]
[![CodeQL (main)][qodeql main badge]][qodeql main]
[![Codecov (main)][coverage main badge]][coverage main]
[![Quality Gate Status (main)][quality main badge]][quality main]
[![GitHub tag (latest)][tag latest badge]][tag latest]

Vcsparser Extensions for SonarQube

## Metrics

- Lines Fixed Over Lines Changed (`vcsparser_linesfixedoverchanged_xx`)
- Number of authors (`vcsparser_numauthors_xx`)
- Number of authors over 10% contribution (`vcsparser_numauthors10perc_xx`)

### Metric Requirements

#### Lines Fixed Over Lines Changed

- `vcsparser_lineschanged_xx`
- `vcsparser_lineschanged_fixes_xx`

#### Number of authors

- `vcsparser_authors_data`

#### Number of authors over 10% contribution

- `vcsparser_authors_data`
- `vcsparser_numchanges_xx`

### Time Period

Metrics in the list above may be suffixed with `_xx`, this represents that there is a metric for each time period.
For instance `vcsparser_linesfixedoverchanged_3m` or `vcsparser_linesfixedoverchanged_1d`.

| Period   | Suffix |
|:---------|:-------|
| 1 Year   | `_1y`  |
| 6 Months | `_6m`  |
| 3 Months | `_3m`  |
| 30 Days  | `_30d` |
| 7 Days   | `_7d`  |
| 1 Day    | `_1d`  |

### Metric Customisation

Options are available to customise the name, description or domain of a metric.

To do so first create a json file in a similar format to bellow. (example [`measures.example.json`](src/test/resources/measures.example.json))

```json
{
    "metrics": [
        {
            "key": "key_of_metric_to_change",
            "name": "The new Name of the metric",
            "description": "The new Description of the metric",
            "domain": "The new Domain of the metric"
        }
    ]
}
```

Now set up an environment variable called `SONAR_VCSPARSER_JSONDATA` that points to this file.

Any changes to the json file will require a restart of SonarQube.

## Prerequisites

- [SonarQube](https://www.sonarqube.org) `>=8.9.2`
- [vcsparser](https://github.com/ericlemes/vcsparser) `>=1.0.88`

## Installation

1. Download the latest build from [GitHub release][tag latest].
2. Move the plugin into plugins folder (`~\extensions\plugins`).
   Make sure to remove any old versions

### vcsparser setup

To publish the metrics from vcsparser you may also need [sonar-generic-metrics](https://github.com/ericlemes/sonar-generic-metrics).
View the documentation on [vcsparser](https://github.com/ericlemes/vcsparser#readme) and [sonar-generic-metrics](https://github.com/ericlemes/sonar-generic-metrics#readme) to lean how.

## License

This project is licensed under the MIT License - see the [license file](LICENSE) for details

[build main]: https://github.com/roryclaasen/sonar-vcsparser-plugin/actions?query=workflow%3A%22Build+%26+Test%22
[build main badge]: https://github.com/roryclaasen/sonar-vcsparser-plugin/workflows/Build%20&%20Test/badge.svg

[qodeql main badge]: https://github.com/roryclaasen/sonar-vcsparser-plugin/workflows/CodeQL/badge.svg
[qodeql main]: https://github.com/roryclaasen/sonar-vcsparser-plugin/actions?query=workflow%3ACodeQL

[coverage main]: https://codecov.io/gh/roryclaasen/sonar-vcsparser-plugin/branch/main
[coverage main badge]: https://img.shields.io/codecov/c/github/roryclaasen/sonar-vcsparser-plugin/main.svg?&logo=codecov

[quality main]: https://sonarcloud.io/dashboard?id=sonar-vcsparser-plugin
[quality main badge]: https://img.shields.io/sonar/https/sonarcloud.io/sonar-vcsparser-plugin/quality_gate.svg?logo=data%3Aimage%2Fpng%3Bbase64%2CiVBORw0KGgoAAAANSUhEUgAAADkAAAA5CAYAAACMGIOFAAAIo0lEQVRo3u2af4wcZRnHP9%2FL5dKcnUnTYNPUiuSQ5nZSCcVGTSEUgygCNYj8UUDBgiI0ml0Cig0hhBBCqjTOGooIiUI00IQfCgKWUBFUoFaCtamzREnTNE1DalPqzoU0l8t9%2FWP2x%2BzeXtu7210h6ZtM7jI78z7vd97ned7v831fONlOtg9NUz%2BMpKVoADgVWAzMA8aBd7H3BeXKxIcWZFoqLLVZK3EJ6DPAMIANalo9Crxp%2B%2FeStgRxsudDAbJaLIxKutP2FaDB7K5zppwz3XJ%2FUtJztu8Iy5VdH0iQaSkasn2HxA9shqZaqoFqzKSw3cTafGzS5qdIG8I4OfqBAVktRovAT0mcewxT48B%2B22MSw6Cl4HmNn902GrPTaE1YTvb%2F30GmpWgJ9h8Ryzr8vB%2F0CPYzSDuDOJnIxeygzXJJlwLrwCMd3t9n9PlwjrGqOSaX%2BTavAWe2Ty5wu8RDQVwZP4EPNWh8DWYjcErbz3uAz4blyqG%2Bg6wWI4DHgbX1kHMWWzsFXw3Kyd5ZfLQlNk%2BAVrX1uVXikiBOJmcz1oHZz6Mvl1gLbiwJEtslVs8GIEAQVw5IukBiW65PJF8EXNfXmUxL0RC4AhrJZYx9wKeDODnUhTgPgb%2BCR3NLzUHbp4flyli%2FZnKtzYiduZNtwOu6ATCb0aRq%2B2qbCdt1G4skfauP7upvN5a%2B7O9vg7jycjcX8LBceQv4dcOOMrtpKeo9yLRYWAxapQY3E6BNvSGd2qQaQmV2Isxoz0EanWd7IHMjA94P%2FKUXGMM42W17V92WbSzO74O7ekUzXwnQK0Gc9LKGeCVnC%2BwVM%2B1hcBZWT2uho%2FDPnpZJopKvXpyz3zOQksLaAl03fEFaKnwU9N8aN90N2hmWk%2FHuwPTBzJ6xBRD2bJ1MS9Fp4OtB64GFxyNEwAvAo8DWubhzWoquAJ7I3RoDPwJ6OIiTXV0BWS1Fi4Xvsblm6sy31Umd3e110LogTv41S5DrbW9uqUDV8KJnsW4LysnbswJZLUUIrgJvtlmgaZ7MV%2FruUArXqxGj08N45i6clqLN4PUd7WX%2Fj4PusH1fWK5MnjDItBgNGG%2BUdOu0kWLvFdppeBc8IWkhsKxWkQx1eOUTQZzsm9maHEGWeEanGQP19dr204Krg3Ll6HETTzVjFJsxN9YDPueahxEPYB6V9E6nWEuLUYi41PbNwMrad9wusW%2FmqZWzbUbbxrBB0rW2R%2BuzWWuXIz2TlqI1QZvHqING80PQvfXqotbJpKQHbd8elitHTtDNBm1fLmnY5smwnMyYWFeLhcdBa%2BsgJXZiViANAjfa3igYRmrMqu1fCF0flJPOINNSYZWtV4HBnN%2B%2FL%2FGNIE6e7qdWmpYKq0B%2FthlQM9BvCuLkwdwzy209L3FqYx01SL4yiCtbpoBMS4VB0D%2BAPAMet7kkLCfb%2BgswWgj8DRhplVI4I2gTt6qlaETwWk3TrbeDhjPCOKm20Dpb37Qd5Xki9s39BlgtFkLbv7M90sJZzW1BB%2FUujJM9Nl%2BzPZF7fhHmlhbumpYKgG9pc%2BJXkB7o8wyOAK8Cq9pLOeCx6cuy5HXg%2FryaAF6flqJ5DZA2qySNSkLKyhpJG3pLvDu66BuSzqqPg2wcb4PWheXkOIlYd4PG6kuKpFNsX5xzV61pLucGeBPY3s9ZtP058KIOSt2Xgjg5bkYPyslhYEvb7TU5kD63KTMA8FQ%2FZ7EWITttxnJxtR1zzgwJxG9ycgkNt68WC4DeAxY0%2BaZXB3HlT11yw3k250s%2BEMTH3uOoFgsrgXUS%2FwY9EMyQBlZL0QLMezmOO2HzEaWlaD6QtvHRj4Xl5EAXAA5hXkOsrLnkd8Jy5aEex%2FZ%2F8gK18ccHgOHWVG0kjnQpzlYar2xKJdzUh9iuti6DDA8CE1LrTkuNYL%2FfhUgbaKtewp7HtjSUr4OMxwdsHwGP127Uk8%2FSLkkXh9tI9JJqMRrooasO2RnzaRQX5tBAWK5M2uy1mwWH5LO65Dp7bE%2FQ%2BHiel6niPXPV5eDBnDxzQGJsoJa%2Bd2R7DqqzjC93SSA%2BKrE767vWP1zUQ1e9uI6hhmNHEFdq66R4qSn5C1uXpaXCgi6Z3marQTOM1qXFqBeuOmC4tokBgBfzjOdZye9LpnYNg27ukv3HkRHZBV6OuKwHzrpW%2BJN1DMjjwJMNkEGcHLF5rCHgZpnp1rQULZuzaXhLaHuuX4BNaTGa38XacyHoxy3jt7bUN24Hcv58L9n5mvrwhoEnqsXCnAYTZvTwzjZBegTxy9r5nrmVZlkd%2FCtgSb4Olri7pdSqzeYe7B%2FlOazNmcDz1WIhnEOskNWp0HZdYfOz6hyAVkuFIaxHgYtrC389Fu8L4uSdKSBrrnU30pv5TCvpPElvpMVo%2BSwADgM%2Fl%2FSTZsZrZj%2BJG5R9xEUz7rtYOFXoJYmrGhQ%2F63MH5q5jCllpKVoKvAFTCME4cD%2BwKYiPzWtrZ3rWSrqLtr2LthNZ9XYYuAf7oeA4O8m1uvO7wPdt5rf1tRd8ThBXDhxXd60WC8uAl8jOw7W3CWAb8KKkXdgHgEmkBcAy26uBrwCLOr%2BrGHzDNBSvCjwH%2FEFSYvtQTY1bZDsCLgC%2BSHY%2Bjym1p7gwjCt7TlhBT0tR7SSGm1KEWzcFcpJlXrrP07gWimfr6rCcbE1L0XLsp8zUsz8d%2B5y6PdAYSxYBetn2lWG5crATlmmDPoiTAxKrQRskxnI%2Bn2NG9XuiTZNpuUBPgz4VlpOttb53AytA90mMt7GUtv%2BbduvxXLcLHAG%2BB1w4HcBjzmSb%2By4GbgFdB17Y%2Bvq0Gz4TwAuSNgKvT6c0VIvRaZmIpq%2BDF3QeYuuhQ0n7bB4G338iYrdmltGiIeMvSLrQ9tmSRmyfUiupqmT7k7tArwq%2FEJQr786sgvD5oNUSZ2b5wKGtSYkjtvdI%2Brvtl0E7wvLsDi6dbCfbyXay9aX9D8CvYfBKDe%2BeAAAAAElFTkSuQmCC

[tag latest]: https://github.com/roryclaasen/sonar-vcsparser-plugin/releases/latest
[tag latest badge]: https://img.shields.io/github/tag/roryclaasen/sonar-vcsparser-plugin.svg?label=latest%20tag&logo=github
