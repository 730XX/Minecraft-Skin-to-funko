/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author leo
 */

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import org.json.JSONArray;
import org.json.JSONObject;

public class SkinFetch{

    public static BufferedImage obtenerSkin(String nombreJugador) throws IOException {
        // UUID
        String uuidURL = "https://api.mojang.com/users/profiles/minecraft/" + nombreJugador;
        String uuidJson = new String(new URL(uuidURL).openStream().readAllBytes(), StandardCharsets.UTF_8);
        JSONObject uuidObj = new JSONObject(uuidJson);
        String uuid = uuidObj.getString("id");
        System.out.println(uuidObj);

        //Texture
        String profileURL = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;
        String profileJson = new String(new URL(profileURL).openStream().readAllBytes(), StandardCharsets.UTF_8);        
        JSONArray props = new JSONObject(profileJson).getJSONArray("properties");
        String encoded = props.getJSONObject(0).getString("value");
        //System.out.println(props);

        //Base64
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        JSONObject decodedJson = new JSONObject(decoded);
        System.out.println(decodedJson);
        String skinURL = decodedJson.getJSONObject("textures").getJSONObject("SKIN").getString("url");
        
        return ImageIO.read(new URL(skinURL));
    }
}

