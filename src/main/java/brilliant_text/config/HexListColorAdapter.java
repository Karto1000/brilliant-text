package brilliant_text.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.NonNullList;

import java.io.IOException;

public class HexListColorAdapter extends TypeAdapter<NonNullList<Integer>> {

    @Override
    public void write(JsonWriter out, NonNullList<Integer> list) throws IOException {
        if (list == null || list.isEmpty()) {
            out.nullValue();
            return;
        }

        out.beginArray();
        for (Integer color : list) {
            if (color != null) {
                out.value(String.format("%08X", color));
            }
        }
        out.endArray();
    }

    @Override
    public NonNullList<Integer> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        NonNullList<Integer> list = NonNullList.create();

        in.beginArray();
        while (in.hasNext()) {
            String hexStr = in.nextString();
            if (hexStr.startsWith("0x") || hexStr.startsWith("0X")) {
                hexStr = hexStr.substring(2);
            }
            int color = (int) Long.parseLong(hexStr, 16);
            list.add(color);
        }
        in.endArray();

        return list;
    }
}
