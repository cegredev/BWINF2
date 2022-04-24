package rechenrätsel;

public class Rechenrätsel {

	public static void main(String[] args) {
		int counter = 0;

		for (int i = 0; i < 10000; i++) {
			var group = new Term(5).populate();

			var equation = Tester.parse(group.toString());

			var tester = new Tester(equation, new Operator[] {Operator.MULTIPLY, Operator.DIVIDE});
			if (tester.getSolutions().size() > 1) {
				tester.printAnalysis();
				counter++;
			}
		}

		System.out.println("There were " + counter + " bad groups!");
	}

}
