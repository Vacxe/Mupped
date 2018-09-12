package core.interfaces;

public interface Mapper<INPUT,  OUTPUT> {
    OUTPUT map(INPUT input);
}
