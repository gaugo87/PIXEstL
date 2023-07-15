# PIXEstL
A program for creating color lithophane and pixel images.
The program relies on a color palette that can be customized by adding its own filaments. This allows for the creation of lithophanes with an infinite variety of filaments.

The filaments can be added to the palette in two ways:

By adding the color of the raw filament (for creating pixel art images).
By adding the chromatic characteristics of the different layers of your filament (for creating lithophanes).

For example for lithophanes, in addition to the usual Cyan, Magenta, and Yellow filaments, the palette allows for the addition of Black (for achieving deep black), or simply adding lighter CMY shades (in addition to the usual ones) to enrich the color palette.

## Usage
```usage: PIXEstL
-c,--colorNumber <arg>            Maximum number of color number. Default: no limits
-cW,--colorPixelWidth <arg>       Width of color pixels (mm). Default: 0.8
-F,--pixelCreationMethod <arg>    Method for pixel creation [ADDITIVE,FULL]. Default: ADDITIVE
-f,--plateThickness <arg>         Thickness of the plate (mm). Default: 0.2
-i,--srcImagePath <arg>           Path to the source image.
-l,--colorLayerNumber <arg>       Number of color pixel layers. Default: 5
-M,--textureMaxThickness <arg>    Maximum thickness of the texture (mm). Default: 2.5
-m,--textureMinThickness <arg>    Minimum  thickness of the texture (mm). Default: 0.2
-n,--layerThreadMaxNumber <arg>   Maximum number of threads for layers generation. Default: 1 by STL layer
-N,--rowThreadMaxNumber <arg>     Number of threads for rows genration. Default: 0
-o,--destZipPath <arg>            Destination ZIP file path. Default: <-image>.zip
-p,--palettePath <arg>            Path to the palette file.
-t,--layerThreadTimeout <arg>     Timeout for layer threads (second). Default: 120
-T,--rowThreadTimeout <arg>       Timeout for row threads (second). Default : 60
-tW,--texturePixelWidth <arg>     Width of texture pixels (mm). Default: 0.25
-w,--destImageWidth <arg>         Width of the destination image (mm).
-z,--noColorLayer <arg>           Color layers will not generated
-Z,--noTextureLayer <arg>         Texture layers will not generated
```

## Examples of results

### Color lithophanes
``` 
java -jar pixelSTL.jar -p palette-0.1mm.json -w 130 -i Cafe_Terrace_at_Night.jpg Cafe_Terrace_at_Night.jpg
```
<img src="attachment/Terrace_at_Night.jpg" width="500" alt="Terrace_at_Night"/>

``` 
java -jar pixelSTL.jar -p filament-palette.json -w 150 -i mem.png
```
<img src="attachment/memory_geisha.jpg" width="500" alt="memory_geisha"/>

```
java -jar pixelSTL.jar -p filament-palette.json -w 150 -i thanos.jpg
```
<img src="attachment/thanos.jpg" width="500" alt="thanos"/>

### Color lithophane with small texture layer
``` 
java -jar pixelSTL.jar -p filament-palette.json -w 130 -M 1.4 -i rainbow_infinity.png
``` 
<img src="attachment/infinity.jpg" width="500" alt="infinity"/>

### Lithophane with only texture layer
``` 
java -jar pixelSTL.jar -p filament-palette.json -M 3 -w 150 -z true -i tsunami_Hokusai.jpg
``` 
<img src="attachment/tsunami_hokusai.jpg" width="750" alt="tsunami_hokusai"/>

### Pixel Art image (with only color layers + FULL colors)
``` 
java -jar pixelSTL.jar -p filament-palette.json -w 200 -c 8 -F FULL -Z true -cW 1 -l 2 -f 3 -i tsunami_Hokusai.jpg
```

## The palette

The palette is composed of a JSON structure that gathers all the filaments you have.
```
"#0086D6":
{
  "name": "Cyan[PLA Basic]",
  "active": true,
  "layers": {
    "5": {
      "H": 202.4,
      "S": 95,
      "L": 48
    },
    [...]
    "2": {
      "H": 202.4,
      "S": 95,
      "L": 69.6
    },
    "1": {
      "H": 202.4,
      "S": 81,
      "L": 79
    }
  }
}
```
- `"#0086D6"`: This is the key that identifies the filament. It is a hexadecimal value representing the filament's color.
  - `"name"`: "Cyan[PLA Basic]": This is the name of the filament. In this example, the name is "Cyan[PLA Basic]".
  - `"active"`: true: This is a boolean indicator to determine if the filament is active (true) or not (false).
  - `"layers"`: This is an object that contains the different layers of the filament.
    - `"5"`, `"4"`, etc. : These are the keys for each layer of the filament. Each layer has an associated number. (Ex: 0.5mm, 0.4mm, 0.3mm if you work with layers of 0.1mm )  
    "H", "S", "L": These are the chromatic properties of each layer.
      - "H" represents the hue value of the layer.
      - "S" represents the saturation value of the layer.
      - "L" represents the lightness value of the layer.

The fields `#XXXXXX`, `name`, and `active` are mandatory. They allow for creating pixel art images.  
The `layers` field is required for the filament to be used in creating color lithophanes through color addition.

Palette composed of BambuLab filaments, with 0.10mm layers :  [filament-palette-0.10mm.json](palette/filament-palette-0.10mm.json)

### How to calculate the chromatic properties of the layers of your filament

1. Create squares in your slicer with the desired thickness, representing the different desired layers. For example, if you are working with 0.1mm layers, create a square with a thickness of 0.1mm, another one with 0.2mm, then 0.3mm, 0.4mm, and 0.5mm.  
   ![](attachment/slicer.png)
2. Print the squares using the appropriate filament.
3. Place each square in front of a neutral light source and take photos of them.
4. Open with your favorite editor the photos (e.g., "Paint") and use the color picker tool to extract the color.
5. Optionally, convert the hexadecimal color to HSL if your editing software does not provide HSL values (e.g., use a tool like https://convertacolor.com/).
   ![](attachment/calibration.png)
6. Enter these HSL values for each layer in the palette.
```
    "2": {
      "H": 202.4,
      "S": 95,
      "L": 69.6
    },
```
## Binary generation
### Prerequisites
- Java JDK (ex: https://www.oracle.com/fr/java/technologies/downloads/)
- Maven (https://maven.apache.org/download.cgi)

### Compilation
``` 
cd PIXEstL
set JAVA_HOME=C:\Program Files\Java\jdk-20
set MAVEN_HOME=C:\Program Files\apache-maven-3.3.1
mvn clean install
```
