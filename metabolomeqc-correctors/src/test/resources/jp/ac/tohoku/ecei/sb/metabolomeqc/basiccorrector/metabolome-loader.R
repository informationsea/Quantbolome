load.metabolome <- function(path) {
    compound <- read.csv(paste(path, "compound.csv", sep="/"), stringsAsFactor=F)
    rownames(compound) <- as.character(compound$ID)

    plate <- read.csv(paste(path, "plate.csv", sep="/"), stringsAsFactor=F)
    rownames(plate) <- as.character(plate$ID)

    sample <- read.csv(paste(path, "sample.csv", sep="/"), stringsAsFactor=F)
    rownames(sample) <- as.character(sample$ID)

    injection <- read.csv(paste(path, "injection.csv", sep="/"), stringsAsFactor=F)
    rownames(injection) <- as.character(injection$ID)

    study <- read.csv(paste(path, "study.csv", sep="/"), stringsAsFactor=F)
    rownames(study) <- as.character(study$ID)

    intensityTable <- read.csv(paste(path, "intensity.csv", sep="/"), stringsAsFactor=F, header=T)
    intensity <- as.matrix(intensityTable[,2:dim(intensityTable)[2]])
    rownames(intensity) <- intensityTable[,1]

    list(compound=compound, plate=plate, sample=sample, injection=injection, intensity=intensity)
}

