source("./metabolome-loader.R")

random.measure <- load.metabolome("random-measure")
global.qc <- random.measure$sample["2", ]

global.qc.injections <- subset(random.measure$injection, SAMPLE_ID == 2 & IGNORED == 0)

plate.compound.cv <- apply(random.measure$intensity, 1, function(x) {
    global.qc.intensity <- x[global.qc.injections$FILENAME]
    by(global.qc.intensity, global.qc.injections$PLATE_ID, function(y) {
        sd(y)/mean(y)
    })
})

compound.cv <- apply(plate.compound.cv, 2, max)
write.csv(compound.cv, "random-measure/compound-cv.csv")
write.csv(t(plate.compound.cv), "random-measure/compound-cv-by-plate.csv")
