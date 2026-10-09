## Transparency:
    Mild usage of generitive ai was used in the devlopment of this project. I did not do so due to any limitations of capability on my part and there is no part of the java and few parts of the html that I do not understand, I simply hate writing html for some reason.

## .wpdi files:
    Short for wide pixel display image, these files are my attempt at freeing this project from google sheets so that it may load bigger images quicker.
    they follow the format of: [header declaring the length of the image]::[body that determines the color of the image].
    Repeated colors may be stored as a run such as `[12*0]` (twelve black cells). The brackets distinguish a run from adjacent numeric color codes. Runs are used only when smaller than the literal colors, so files without useful repetition stay in the original format. The viewer reads original files and unambiguous previous `count*color` runs; older ambiguous compressed files should be regenerated from their source image.

## canvas.html
    An html file designed to display the .wpdi images far more efficiantly than google sheets could, aswell as allowing for them to be downloaded as a .png.
    the individual cells that make up the image are exactly twice as long as they are tall, giving the files their name
