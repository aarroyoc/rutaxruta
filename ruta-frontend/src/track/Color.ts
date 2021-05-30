export class Color {
    colors = [
        "#FF0000",
        "#FF1100",
        "#FF2200",
        "#FF3300",
        "#FF4400",
        "#FF5500",
        "#FF6600",
        "#FF7700",
        "#FF8800",
        "#FF9900",
        "#FFAA00",
        "#FFBB00",
        "#FFCC00",
        "#FFDD00",
        "#FFEE00",
        "#FFFF00",
        "#EEFF00",
        "#DDFF00",
        "#CCFF00",
        "#BBFF00",
        "#AAFF00",
        "#99FF00",
        "#88FF00",
        "#77FF00",
        "#66FF00",
        "#55FF00",
        "#44FF00",
        "#33FF00",
        "#22FF00",
        "#11FF00",
        "#00FF00"
    ];

    getColor(value: number): string {
        const index = Math.floor(value*this.colors.length);
        if(index >= this.colors.length){
            return this.colors[this.colors.length-1];
        }
        return this.colors[index];
    }

}