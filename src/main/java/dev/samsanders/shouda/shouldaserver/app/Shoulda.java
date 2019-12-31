package dev.samsanders.shouda.shouldaserver.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Shoulda {

    private final String text;

    @JsonCreator
    public Shoulda(@JsonProperty("text") String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shoulda shoulda = (Shoulda) o;
        return Objects.equals(text, shoulda.text);
    }

    @Override
    public int hashCode() {

        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return "Shoulda{" +
                "text='" + text + '\'' +
                '}';
    }
}
