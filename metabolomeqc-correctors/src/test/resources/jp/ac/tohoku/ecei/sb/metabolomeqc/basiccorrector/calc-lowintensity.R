source("./metabolome-loader.R")

random.measure <- load.metabolome("flat-measure-nodrift")

injections <- subset(random.measure$injection, random.measure$sample[SAMPLE_ID,]$SAMPLETYPE == "QC" & IGNORED == 0)

low.intensity.count <- apply(random.measure$intensity, 1, function(x) {
    intensity <- x[injections$FILENAME]
    length(intensity[intensity < 0.001])
})

write.csv(low.intensity.count, "flat-measure-nodrift/lowintensity-count.csv")
