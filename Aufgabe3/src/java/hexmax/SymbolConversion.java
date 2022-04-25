package hexmax;

import hexmax.alphabet.Symbol;

import java.util.List;

/**
 * Eine Umwandlung zwischen zwei Symbolen.
 *
 * @param additions Die Anzahl an Ergänzungen an Stellen.
 * @param removals Die Anzahl an Entnahmen an Stellen.
 * @param target Das Symbol, zu dem ein anderes umgewandelt wird.
 */
public record SymbolConversion(int additions, int removals, Symbol target, List<Integer> missingBits, List<Integer> spareBits) {
}
