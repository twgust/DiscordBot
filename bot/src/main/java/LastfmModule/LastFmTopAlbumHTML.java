package LastfmModule;

import de.umass.lastfm.Caller;

import java.io.*;

public class LastFmTopAlbumHTML {

    public boolean createHTMLfile(String [][] array,  int rowColSize, int colSize){

        String[][] albums = array;
        int size = rowColSize;
        if(rowColSize < 1){
            return false;
        }



        /*
        int colSize = rowColSize;
        if(rowColSize < Math.sqrt(Math.round(albums.length)) && rowColSize < 10){
            colSize++;
        }

         */


        StringBuilder div = new StringBuilder();

        int counter = 0;
        for(int i = 0; i < rowColSize; i +=0){
            StringBuilder result = new StringBuilder();
            result.append("<div class=\"grid\">\n" + "      <div class=\"row\">\n");
            for (int j = 0; j < colSize; j++){
                if (counter < albums.length){
                    String artist = albums[counter][0];
                    String albumName = albums[counter][1];
                    String playcount = albums[counter][2];
                    String imageURL = albums[counter][3];

                    result.append("        <div class=\"container\">\n" + "          <img src=\"");
                    result.append(imageURL);
                    result.append("\" width=300 height=300>\n");
                    result.append("          <div class=\"text\">\n");
                    result.append("            ");
                    result.append(artist);
                    result.append("<br>\n");
                    result.append("            ");
                    result.append(albumName);
                    result.append("<br>\n");
                    result.append("            plays: ");
                    result.append(playcount);
                    result.append("\n");
                    result.append("        </div>\n");
                    result.append("      </div>\n");
                }
                counter++;
            }
            div.append(result);
            i++;
        }
        System.out.println(counter);
        System.out.println(albums.length);


        String htmlbody = "<html>\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "  </head>\n" +
                "  <style>\n" +
                "    div {\n" +
                "        font-size: 0px;\n" +
                "        overflow: hidden;\n" +
                "        text-overflow: ellipsis;\n" +
                "        white-space: nowrap;\n" +
                "}\n" +
                "\n" +
                "body {\n" +
                "        display: block;\n" +
                "        margin: 0px;\n" +
                "}\n" +
                "\n" +
                ".grid {\n" +
                "\tbackground-color: #ffc5c8;\n" +
                "}\n" +
                "\n" +
                ".container {\n" +
                "\twidth: 300px;\n" +
                "\tdisplay: inline-block;\n" +
                "\tposition: relative;\n" +
                "}\n" +
                "\n" +
                ".text {\n" +
                "\twidth: 299px;\n" +
                "\tposition: absolute;\n" +
                "\ttext-align: left;\n" +
                "\tline-height: 1;\n" +
                "\t\n" +
                "\tfont-family: 'Helvetica', 'Serif';\n" +
                "\tfont-size: 16px;\n" +
                "\tfont-weight: medium;\n" +
                "\tcolor: white;\n" +
                "\ttext-shadow: 1px 1px black;\n" +
                "\t\n" +
                "\ttop: 2px;\n" +
                " \tleft: 2px;\n" +
                "\tright:2px;\n" +
                "}\n" +
                "  </style>\n" +
                "  <body>\n" +
                div.toString().toString()+
                "  </body>\n" +
                "</html>";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("testimages/album.html"));
            bw.write(htmlbody);
            bw.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createJSFile(int dimensionHeight, int dimensionWidth){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("testimages/html.js"));
            bw.write("var page = require('webpage').create();\n" +
                    "page.viewportSize = {\n" +
                    "\twidth: "+dimensionWidth+",\n" +
                    "\theight: "+dimensionHeight+"\n" +
                    "\t};\n" +
                    "page.open('file:///C:/Users/Robert/Documents/GitHub/DiscordBot/bot/testimages/album.html', function() {\n" +
                    "  page.render('C:/Users/Robert/Documents/GitHub/DiscordBot/bot/testimages/image.jpg');\n" +
                    "  phantom.exit();\n" +
                    "});");
            bw.flush();
            bw.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean runJSFile() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd.exe", "/c", "cd C:\\Users\\Robert\\Documents\\GitHub\\DiscordBot\\bot\\testimages && phantomjs html.js");

            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine())!=null){
                output.append(line);
                output.append("\n");
            }
            br.close();
            int exitVal = process.waitFor();
            if(exitVal == 0){
                System.out.println(0);
                System.out.println(output);
                return true;
            }
            else {
                System.out.println(exitVal);
                System.out.println(output);
                return false;
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void main(String[] args) throws IOException {
        LastFmTopAlbumHTML topAlbumHTML = new LastFmTopAlbumHTML();
        String[][]array = new String[97][4];
        for (int i = 0; i < array.length; i++){
            if(i == 0 || i == 1){
                array[i][0] = "artist";
                array[i][1] = "album";
                array[i][2] = "playcount";
                array[i][3] = "https://lastfm.freetls.fastly.net/i/u/300x300/bc85d77d2746baca88059f32c12395ec.jpg";
            }
            else {
                array[i][0] = "artist";
                array[i][1] = "album";
                array[i][2] = "playcount";
                array[i][3] = "https://lastfm.freetls.fastly.net/i/u/300x300/29a0e7984be76ad9bd0b047f7de2242f.jpg";
            }
        }
        topAlbumHTML.createHTMLfile(array, 10, 10);
        topAlbumHTML.createJSFile(300*10, 300*10);
        topAlbumHTML.runJSFile();
    }
}
