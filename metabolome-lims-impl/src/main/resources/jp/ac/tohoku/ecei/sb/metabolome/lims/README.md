Metabolome Dataset
==================

File Overview
----------------

1. study.csv : Information of this study
2. plate.csv : List of plates
3. sample.csv : List of samples
4. injection.csv : List of injections
5. injection-human-friendly.csv : List of injection with sample information. This file is ignored while reading data.
6. compound.csv : List of compounds
7. intensity.csv : Intensity matrix


File Format
-----------

### study.csv

|  #|Field Name    |Description                                        |
|--:|:-------------|:--------------------------------------------------|
|  1|NAME          |Name of the study (Not required)                   |
|  2|ID            |Integer ID of the study                            |
|  3|ATTRIBUTE_JSON|JSON representation of comments or other attributes|

### plate.csv

|  #|Field Name    |
|--:|:-------------|
|  1|STUDY_ID      |
|  2|NAME          |
|  3|DATETIME      |
|  4|ID            |
|  5|ATTRIBUTE_JSON|

### sample.csv

|  #|Field Name    |Description                 
|--:|:-------------|:--------------------------------------------------------------------------------------------------|
|  1|SAMPLETYPE    |One of BLANK, QC, NORMAL, UNKNOWN                                                                  |
|  2|UUID          |UUIDv4. If you want to use this sample in the other study, please use same UUID for the same sample|
|  3|NAME          |Name of study (Not required)                                                                       |
|  4|ID            |Integer ID of this sample                                                                          |
|  5|ATTRIBUTE_JSON|JSON representation of comments or other attributes                                                |

### injection.csv

|  #|Field Name    |Description                                                                   |
|--:|:-------------|:-----------------------------------------------------------------------------|
|  1|PLATE_ID      |The plate ID that this injection belongs to                                   |
|  2|NAME          |Name of this injection (Not required)                                         |
|  3|FILENAME      |File name of raw files. This is appeared in the third line of progenesis CSV  |
|  4|SAMPLE_ID     |The ID of the sample.                                                         |
|  5|RUNINDEX      |The order of run                                                              |
|  6|IGNORED       |Set 1 if this injection should be ignored.                                    |
|  7|QCINDEX       |The index of QC in the plate. Required if the sample is one of Quality Control|
|  8|ID            |Integer ID of this injection (usually, equal to Run Index)                    |
|  9|ATTRIBUTE_JSON|JSON representation of comments or other attributes                           |

### compound.csv

|  #|Field Name   |Description
|--:|:------------|:-----------
|  1|MZ           |
|  2|RETENTIONTIME|
|  3|NEUTRALMASS  |
|  4|CHARGE       |
|  5|ID           |


How to verify data
------------------

* OS X: `shasum -a 256 -c sha256sum.txt`
* Linux: `sha256sum -c sha256sum.txt`


