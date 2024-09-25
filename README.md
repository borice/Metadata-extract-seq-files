[![Scala CI](https://github.com/htrc/Metadata-extract-seq-files/actions/workflows/ci.yml/badge.svg)](https://github.com/htrc/Metadata-extract-seq-files/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/htrc/Metadata-extract-seq-files/graph/badge.svg?token=eAU8RK60Yd)](https://codecov.io/gh/htrc/Metadata-extract-seq-files)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/htrc/Metadata-extract-seq-files?include_prereleases&sort=semver)](https://github.com/htrc/Metadata-extract-seq-files/releases/latest)

# Metadata-extract-seq-files
This tool can be used to extract sequence files

# Build
`sbt clean stage` - generates the unpacked, runnable application in the `target/universal/stage/` folder.  
`sbt clean universal:packageBin` - generates an application ZIP file

# Usage
*Note:* Must use one of the supported JVMs for Apache Spark (at this time Java 8 through Java 11 are supported)
```text
extract-seq-files <version>
HathiTrust Research Center
  -c, --cores  <N>                      (Optional) The number of CPU cores to
                                        use (if not specified, uses all
                                        available cores)
  -f, --format  <pairtree|stubbytree>   (Optional) One of pairtree or stubbytree
                                        (default = pairtree)
  -l, --log-level  <LEVEL>              (Optional) The application log level;
                                        one of INFO, DEBUG, OFF (default = INFO)
  -o, --output  <DIR>                   Write the output to DIR
      --spark-log  <FILE>               (Optional) Where to write logging output
                                        from Spark to
  -h, --help                            Show help message
  -v, --version                         Show version of this program

 trailing arguments:
  input (required)   The path to the folder containing the sequence file parts
```
