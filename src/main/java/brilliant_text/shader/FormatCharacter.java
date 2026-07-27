package brilliant_text.shader;

import lombok.Getter;

@Getter
public class FormatCharacter {
    private final char character;

    // These characters cannot be assigned since they are already used by vanilla minecraft
    public static final String DISALLOWED_CHARS = "0123456789abcdefklmnor";

    private FormatCharacter(char character) {
        this.character = character;
    }

    public static boolean isInvalidCharacter(char character) {
        return DISALLOWED_CHARS.indexOf(character) != -1;
    }

    public static FormatCharacter tryFrom(char character) throws IllegalArgumentException {
        if (isInvalidCharacter(character))
            throw new IllegalArgumentException(
                    String.format(
                            "Character '%c' is not allowed since it is used by vanilla Minecraft formatting. Disallowed characters are: %s",
                            character,
                            DISALLOWED_CHARS
                    )
            );

        return new FormatCharacter(character);
    }


    @Override
    public String toString() {
        return String.valueOf(this.character);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FormatCharacter)) return false;
        FormatCharacter c = (FormatCharacter) obj;
        return c.character == this.character;
    }

    @Override
    public int hashCode() {
        return this.character;
    }
}
