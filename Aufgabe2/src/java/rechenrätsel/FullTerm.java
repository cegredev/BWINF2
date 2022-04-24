package rechenrätsel;

public class FullTerm {

	private final Term[] terms;
	private final Operator[] operators;

	private int result;

	public FullTerm(int operatorCount) {
		this.terms = new Term[operatorCount / 4];
		this.operators = new Operator[terms.length - 1];
	}

	public FullTerm populate() {
		int acc = (terms[0] = new Term(4).populate()).getResult();

		for (int i = 1; i < terms.length; i++) {
			terms[i] = new Term(4);
			operators[i - 1] = Math.random() < 0.5 ? Operator.ADD : Operator.SUBTRACT;
		}

		this.result = acc;

		return this;
	}

	@Override
	public String toString() {
		var builder = new StringBuilder();

		builder.append(terms[0].termAsString());
		for (int i = 1; i < terms.length; i++)
			builder.append(operators[i - 1]).append(terms[i]);

		builder.append("=").append(result);

		return builder.toString();
	}

}
