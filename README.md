# Sonar Vcsparser Plugin

[![Build status](https://ci.appveyor.com/api/projects/status/tsgtxp63lee7hk1j?svg=true)](https://ci.appveyor.com/project/roryclaasen/sonar-vcsparser-plugin)
[![AppVeyor tests](https://img.shields.io/appveyor/tests/roryclaasen/sonar-vcsparser-plugin.svg?logo=appveyor)](https://ci.appveyor.com/project/roryclaasen/sonar-vcsparser-plugin/build/tests)
[![codecov](https://codecov.io/gh/roryclaasen/sonar-vcsparser-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/roryclaasen/sonar-vcsparser-plugin)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sonar-vcsparser-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=Asonar-vcsparser-plugin)
[![GitHub tag (latest SemVer)](https://img.shields.io/github/tag/roryclaasen/sonar-vcsparser-plugin.svg)](https://github.com/roryclaasen/sonar-vcsparser-plugin/releases/latest)

Vcsparser Extensions for SonarQube

## Metrics

- Lines Fixed Over Lines Changed (`vcsparser_linesfixedoverchanged_xx`)

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

- [SonarQube](https://www.sonarqube.org) `>=6.7.4`
- [vcsparser](https://github.com/ericlemes/vcsparser)

## Installation

1. Download either the latest build from [GitHub release](https://github.com/roryclaasen/sonar-vcsparser-plugin/releases/latest) or from [AppVeyor](https://ci.appveyor.com/project/roryclaasen/sonar-vcsparser-plugin/build/artifacts).
2. Move the plugin into plugins folder (`~\extensions\plugins`).
   Make sure to remove any old versions

### vcsparser setup

To publish the metrics from vcsparser you may also need [sonar-generic-metrics](https://github.com/ericlemes/sonar-generic-metrics).
Checkout documentation on [vcsparser](https://github.com/ericlemes/vcsparser#readme) and [sonar-generic-metrics](https://github.com/ericlemes/sonar-generic-metrics#readme).

## License

This project is licensed under the MIT License - see the [license file](LICENSE) for details
