# Locations
OUTPUT_DIR := bin 

# Tools
RM := rm -rf

# The make rules
all: build

# Builds the project, along with all of the dependencies, into a single jar
build:
	sbt assembly

clean:
	sbt clean
	$(RM) target $(OUTPUT_DIR) *.s

.PHONY: all clean

