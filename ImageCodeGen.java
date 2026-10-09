import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

public class ImageCodeGen {
    private static String encodeColorCode(char[] colorCode) {
        StringBuilder encoded = new StringBuilder(colorCode.length);
        for (int start = 0; start < colorCode.length;) {
            int end = start + 1;
            while (end < colorCode.length && colorCode[end] == colorCode[start]) {
                end++;
            }

            int runLength = end - start;
            int encodedRunLength = Integer.toString(runLength).length() + 4;
            if (runLength > encodedRunLength) {
                encoded.append('[').append(runLength).append('*').append(colorCode[start]).append(']');
            } else {
                encoded.append(colorCode, start, runLength);
            }
            start = end;
        }

        return encoded.length() < colorCode.length
            ? encoded.toString()
            : new String(colorCode);
    }

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                throw new IOException("Provide an image file path.");
            }
            Path inputPath = Path.of((String)args[0]).toAbsolutePath().normalize();
            File file = inputPath.toFile();
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                throw new IOException("Unsupported or unreadable image: " + inputPath);
            }

            int imageWidth = image.getWidth();
            int ImageHeight = image.getHeight();
            
            
            String WPDIFolder = "C:/Users/Kaden/Pictures/wide-pixel-display-images/";
             
            // 16:9 = 16:18
            double segmentAspectRatio = imageWidth / (2.0 * ImageHeight);
            int segmentsX;
            int segmentsY;

            int MaxX = 160;
            if(args.length>1){MaxX = Integer.parseInt(args[1]);}
            int MaxY = Math.round((18*(MaxX/16)));

            if (segmentAspectRatio >= MaxX / MaxY) {
                segmentsX = MaxX;
                segmentsY = Math.max(1, (int) Math.round(segmentsX / segmentAspectRatio));
            } else {
                segmentsY = MaxY;
                segmentsX = Math.max(1, (int) Math.round(segmentsY * segmentAspectRatio));
            }

            float BWThreshold = 0.25f;
            
            char[] colorCode = new char[segmentsX * segmentsY];
            int colorIndex = 0;
            float[] hsb = new float[3];
            for(int i=0;i<segmentsY;i++){for(int t=0;t<segmentsX;t++){   
                

                int sampleX = (int) (((long) (2 * t + 1) * imageWidth) / (2L * segmentsX));
                int sampleY = (int) (((long) (2 * i + 1) * ImageHeight) / (2L * segmentsY));
                int colorAtSpot = image.getRGB(sampleX, sampleY);
                int red = (colorAtSpot >> 16) & 0xff;
                int green = (colorAtSpot >> 8) & 0xff;
                int blue = colorAtSpot & 0xff;
                // System.out.println(i*(maxX/segmentsX));
                // System.out.println(t*(maxY/segmentsY));
           
                int colorAvrg = (blue + green + red) / 3;
                //System.out.println(RGB);


                // Classify chromatic colors by hue, then use saturation and brightness
                // to distinguish neutral colors and the darker palette variants.
                //       RGB to Hue Saturation Brightness
                Color.RGBtoHSB(red, green, blue, hsb);
                char compressedColor;
                //black/white check
                if (hsb[1] < BWThreshold*0.75) {
                    compressedColor = hsb[2] >= 0.5f ? '1' : '0'; // white or black
                //second black/white check
                } else if(Math.abs(red - colorAvrg) <= colorAvrg * BWThreshold
                    && Math.abs(blue - colorAvrg) <= colorAvrg * BWThreshold
                    && Math.abs(green - colorAvrg) <= colorAvrg * BWThreshold){
                        compressedColor='0';//black

                        if(colorAvrg>128){
                            compressedColor='1';//white
                    }
                } else {
                    float hue = hsb[0];
                    if (hue < 0.04f || hue >= 0.92f) {
                        compressedColor = '5'; // red
                    } else if (hue < 0.20f) {
                        compressedColor = '4'; // yellow
                    } else if (hue < 0.42f) {
                        compressedColor = '3'; // green
                    } else if (hue < 0.70f) {
                        compressedColor = '2'; // blue
                    } else {
                        compressedColor = '!'; // purple
                    }

                    // Codes 2-5 have corresponding darker variants 6-9.
                    if (compressedColor != '!' && hsb[2] < 0.5f) {
                        compressedColor = (char) (compressedColor + 4);
                    }
                }
                
                if((colorAtSpot >>> 24) < 100){
                    compressedColor=' ';
                }
                colorCode[colorIndex++] = compressedColor;
                    }
                }
                // System.out.println(colorCode);
                    // Toolkit.getDefaultToolkit().getSystemClipboard()
                    //     .setContents(new StringSelection(colorCode), null);
                // System.out.println("width: "+segmentsX);
                try {
                     //make .wpdi file
                     String inputFileName = inputPath.getFileName().toString();
                     int extensionIndex = inputFileName.lastIndexOf('.');
                     String outputFileName = extensionIndex > 0
                         ? inputFileName.substring(0, extensionIndex) + ".wpdi"
                         : inputFileName + ".wpdi";
                     Files.writeString(
                         Path.of(WPDIFolder, outputFileName),
                         segmentsX + "::" + encodeColorCode(colorCode)
                     );
                     System.out.println("File created and written successfully!");
                 } catch (IOException e) {
                     System.err.println("An error occurred: " + e.getMessage());
                 }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}
