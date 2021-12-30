from typing import Optional


operators = ["+", "-", "*", "/"]


class Pair:
    def __init__(self, operator: Optional[str], operand: int) -> None:
        self.operator = operator
        self.operand = operand

    def __str__(self) -> str:
        op = self.operator
        return (op if op else "") + str(self.operand)

    def __repr__(self) -> str:
        return f"[{self.operator}{self.operand}]"


def main() -> None:
    term = input()
    solve(term)


def equation_to_str(equation: list[Pair]) -> str:
    return "".join((str(pair) for pair in equation))


def solve_rec(equation: list[Pair], index: int, result: int) -> None:
    if index == len(equation):
        term = equation_to_str(equation)
        if result == eval(term):
            for i in range(len(equation)):
                if eval(term[:1 + 2 * i]) % 1 != 0:
                    return

            print(f"Solved with {term}")
    else:
        pair = equation[index]

        for operator in operators:
            pair.operator = operator
            solve_rec(equation, index + 1, result)


def solve(term: str) -> None:
    term, result = term.split("=")
    operands = [int(o) for o in term[::2]]

    equation = [Pair(None, operands[0])]
    for operand in operands[1:]:
        equation.append(Pair("+", operand))

    solve_rec(equation, 1, int(result))


if __name__ == "__main__":
    main()
