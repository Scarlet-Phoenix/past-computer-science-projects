"""Unit tests for student implementations of Weighted A* search.

This module provides tests for the functions students must implement:
- Node class
- expand(problem, node)
- solution(node)
- insert(frontier, node, f, counter)
- pop(frontier)
- best_first_search(problem, f)

Usage:
    # Import your implementations
    from your_module import Node, expand, solution, insert, pop, best_first_search

    # Run the tests
    from comp372_lab1_starter.tests import run_all_tests
    run_all_tests(Node, expand, solution, insert, pop, best_first_search)

    # Or run individual test suites
    from comp372_lab1_starter.tests import test_node, test_expand, ...
    test_node(Node)
"""
from __future__ import annotations

from typing import Any, Callable, Dict, List, Optional, Tuple


class SimpleSearchProblem:
    """A simple search problem for testing (no map data required).

    Graph structure:
        A --1-- B --2-- C
        |       |
        4       3
        |       |
        D --1-- E --1-- F (goal)

    Coordinates (for heuristic testing):
        A: (0, 0), B: (0, 1), C: (0, 2)
        D: (1, 0), E: (1, 1), F: (1, 2)
    """

    def __init__(self):
        self._initial = "A"
        self._goal = "F"
        self._edges: Dict[str, List[Tuple[str, float]]] = {
            "A": [("B", 1.0), ("D", 4.0)],
            "B": [("A", 1.0), ("C", 2.0), ("E", 3.0)],
            "C": [("B", 2.0)],
            "D": [("A", 4.0), ("E", 1.0)],
            "E": [("B", 3.0), ("D", 1.0), ("F", 1.0)],
            "F": [("E", 1.0)],
        }
        self._coords: Dict[str, Tuple[float, float]] = {
            "A": (0.0, 0.0),
            "B": (0.0, 1.0),
            "C": (0.0, 2.0),
            "D": (1.0, 0.0),
            "E": (1.0, 1.0),
            "F": (1.0, 2.0),
        }

    @property
    def initial(self) -> str:
        return self._initial

    @property
    def goal(self) -> str:
        return self._goal

    def get_coordinates(self, state: str) -> Tuple[float, float]:
        return self._coords[state]

    def actions(self, state: str) -> List[str]:
        return [neighbor for neighbor, _ in self._edges.get(state, [])]

    def result(self, state: str, action: str) -> str:
        return action

    def action_cost(self, s: str, action: str, s_prime: str) -> float:
        for neighbor, cost in self._edges.get(s, []):
            if neighbor == s_prime:
                return cost
        return float("inf")


def simple_heuristic(problem: SimpleSearchProblem, state: str) -> float:
    """Manhattan distance heuristic for the simple problem."""
    x1, y1 = problem.get_coordinates(state)
    x2, y2 = problem.get_coordinates(problem.goal)
    return abs(x2 - x1) + abs(y2 - y1)


def simple_goal_test(problem: SimpleSearchProblem, state: str) -> bool:
    """Goal test for the simple problem."""
    return state == problem.goal


class TestResult:
    """Result of a single test."""

    def __init__(self, name: str, passed: bool, message: str = ""):
        self.name = name
        self.passed = passed
        self.message = message

    def __str__(self) -> str:
        status = "PASS" if self.passed else "FAIL"
        msg = f" - {self.message}" if self.message else ""
        return f"[{status}] {self.name}{msg}"


def test_node(Node) -> List[TestResult]:
    """Test the Node class implementation."""
    results = []

    # Test 1: Node initialization with defaults
    try:
        node = Node(state="A")
        assert node.state == "A", f"Expected state='A', got {node.state}"
        assert node.parent is None, f"Expected parent=None, got {node.parent}"
        assert node.path_cost == 0.0, f"Expected path_cost=0.0, got {node.path_cost}"
        results.append(TestResult("Node.__init__ with defaults", True))
    except Exception as e:
        results.append(TestResult("Node.__init__ with defaults", False, str(e)))

    # Test 2: Node initialization with all parameters
    try:
        parent = Node(state="A")
        child = Node(state="B", parent=parent, path_cost=5.0)
        assert child.state == "B", f"Expected state='B', got {child.state}"
        assert child.parent is parent, "Parent reference incorrect"
        assert child.path_cost == 5.0, f"Expected path_cost=5.0, got {child.path_cost}"
        results.append(TestResult("Node.__init__ with all params", True))
    except Exception as e:
        results.append(TestResult("Node.__init__ with all params", False, str(e)))

    # Test 3: Node attributes are accessible
    try:
        node = Node(state="X", parent=None, path_cost=10.0)
        _ = node.state
        _ = node.parent
        _ = node.path_cost
        results.append(TestResult("Node attributes accessible", True))
    except AttributeError as e:
        results.append(TestResult("Node attributes accessible", False, str(e)))

    return results


def test_expand(Node, expand) -> List[TestResult]:
    """Test the expand function."""
    results = []
    problem = SimpleSearchProblem()

    # Test 1: Expand initial node
    try:
        node = Node(state="A")
        children = expand(problem, node)
        assert isinstance(children, list), "expand should return a list"
        assert len(children) == 2, f"Expected 2 children from A, got {len(children)}"
        child_states = {c.state for c in children}
        assert child_states == {"B", "D"}, f"Expected children B,D, got {child_states}"
        results.append(TestResult("expand returns correct children", True))
    except Exception as e:
        results.append(TestResult("expand returns correct children", False, str(e)))

    # Test 2: Check parent pointers
    try:
        node = Node(state="A")
        children = expand(problem, node)
        for child in children:
            assert child.parent is node, "Child parent should reference parent node"
        results.append(TestResult("expand sets parent correctly", True))
    except Exception as e:
        results.append(TestResult("expand sets parent correctly", False, str(e)))

    # Test 3: Check path costs
    try:
        node = Node(state="A", path_cost=0.0)
        children = expand(problem, node)
        costs = {c.state: c.path_cost for c in children}
        assert costs["B"] == 1.0, f"Expected B cost=1.0, got {costs['B']}"
        assert costs["D"] == 4.0, f"Expected D cost=4.0, got {costs['D']}"
        results.append(TestResult("expand calculates path_cost correctly", True))
    except Exception as e:
        results.append(TestResult("expand calculates path_cost correctly", False, str(e)))

    # Test 4: Expand from non-initial node with accumulated cost
    try:
        parent = Node(state="A", path_cost=0.0)
        node = Node(state="B", parent=parent, path_cost=1.0)
        children = expand(problem, node)
        costs = {c.state: c.path_cost for c in children}
        # From B: A(1), C(2), E(3) - costs should be 1.0 + edge_cost
        assert costs["A"] == 2.0, f"Expected A cost=2.0, got {costs['A']}"
        assert costs["C"] == 3.0, f"Expected C cost=3.0, got {costs['C']}"
        assert costs["E"] == 4.0, f"Expected E cost=4.0, got {costs['E']}"
        results.append(TestResult("expand accumulates path_cost", True))
    except Exception as e:
        results.append(TestResult("expand accumulates path_cost", False, str(e)))

    # Test 5: Expand node with no successors
    try:
        node = Node(state="C")
        children = expand(problem, node)
        # C only connects back to B
        assert len(children) == 1, f"Expected 1 child from C, got {len(children)}"
        results.append(TestResult("expand handles limited successors", True))
    except Exception as e:
        results.append(TestResult("expand handles limited successors", False, str(e)))

    return results


def test_solution(Node, solution) -> List[TestResult]:
    """Test the solution function."""
    results = []

    # Test 1: Solution from single node (initial = goal)
    try:
        node = Node(state="A")
        path = solution(node)
        assert path == ["A"], f"Expected ['A'], got {path}"
        results.append(TestResult("solution single node", True))
    except Exception as e:
        results.append(TestResult("solution single node", False, str(e)))

    # Test 2: Solution from two-node path
    try:
        n1 = Node(state="A")
        n2 = Node(state="B", parent=n1, path_cost=1.0)
        path = solution(n2)
        assert path == ["A", "B"], f"Expected ['A', 'B'], got {path}"
        results.append(TestResult("solution two nodes", True))
    except Exception as e:
        results.append(TestResult("solution two nodes", False, str(e)))

    # Test 3: Solution from longer path
    try:
        n1 = Node(state="A")
        n2 = Node(state="D", parent=n1, path_cost=4.0)
        n3 = Node(state="E", parent=n2, path_cost=5.0)
        n4 = Node(state="F", parent=n3, path_cost=6.0)
        path = solution(n4)
        assert path == ["A", "D", "E", "F"], f"Expected ['A','D','E','F'], got {path}"
        results.append(TestResult("solution longer path", True))
    except Exception as e:
        results.append(TestResult("solution longer path", False, str(e)))

    # Test 4: Solution returns list type
    try:
        node = Node(state="X")
        path = solution(node)
        assert isinstance(path, list), f"Expected list, got {type(path)}"
        results.append(TestResult("solution returns list", True))
    except Exception as e:
        results.append(TestResult("solution returns list", False, str(e)))

    return results


def test_insert_and_pop(Node, insert, pop) -> List[TestResult]:
    """Test insert and pop functions together."""
    results = []

    # Test 1: Insert and pop single node
    try:
        frontier = []
        counter = [0]
        node = Node(state="A", path_cost=5.0)
        insert(frontier, node, lambda n: n.path_cost, counter)
        assert len(frontier) == 1, "Frontier should have 1 element"
        popped = pop(frontier)
        assert popped.state == "A", f"Expected state A, got {popped.state}"
        assert len(frontier) == 0, "Frontier should be empty after pop"
        results.append(TestResult("insert/pop single node", True))
    except Exception as e:
        results.append(TestResult("insert/pop single node", False, str(e)))

    # Test 2: Pop returns lowest f-value first
    try:
        frontier = []
        counter = [0]
        n1 = Node(state="A", path_cost=10.0)
        n2 = Node(state="B", path_cost=5.0)
        n3 = Node(state="C", path_cost=15.0)
        f = lambda n: n.path_cost
        insert(frontier, n1, f, counter)
        insert(frontier, n2, f, counter)
        insert(frontier, n3, f, counter)
        first = pop(frontier)
        assert first.state == "B", f"Expected B (lowest cost), got {first.state}"
        second = pop(frontier)
        assert second.state == "A", f"Expected A (second lowest), got {second.state}"
        third = pop(frontier)
        assert third.state == "C", f"Expected C (highest cost), got {third.state}"
        results.append(TestResult("pop returns lowest f-value first", True))
    except Exception as e:
        results.append(TestResult("pop returns lowest f-value first", False, str(e)))

    # Test 3: Counter increments for tiebreaking
    try:
        frontier = []
        counter = [0]
        n1 = Node(state="A", path_cost=5.0)
        n2 = Node(state="B", path_cost=5.0)
        insert(frontier, n1, lambda n: n.path_cost, counter)
        insert(frontier, n2, lambda n: n.path_cost, counter)
        assert counter[0] == 2, f"Counter should be 2, got {counter[0]}"
        # With equal f-values, first inserted should come out first (FIFO tiebreak)
        first = pop(frontier)
        assert first.state == "A", f"Expected A (inserted first), got {first.state}"
        results.append(TestResult("tiebreaking with counter", True))
    except Exception as e:
        results.append(TestResult("tiebreaking with counter", False, str(e)))

    # Test 4: Custom f function
    try:
        frontier = []
        counter = [0]
        n1 = Node(state="A", path_cost=10.0)
        n2 = Node(state="B", path_cost=5.0)
        # f = path_cost + 10 for A, path_cost + 0 for B
        f = lambda n: n.path_cost + (10 if n.state == "A" else 0)
        insert(frontier, n1, f, counter)  # f(A) = 20
        insert(frontier, n2, f, counter)  # f(B) = 5
        first = pop(frontier)
        assert first.state == "B", f"Expected B (lower f), got {first.state}"
        results.append(TestResult("custom f function", True))
    except Exception as e:
        results.append(TestResult("custom f function", False, str(e)))

    return results


def test_best_first_search(Node, expand, solution, insert, pop, best_first_search) -> List[TestResult]:
    """Test the best_first_search function."""
    results = []
    problem = SimpleSearchProblem()

    # Test 1: Find path with uniform cost (Dijkstra's)
    try:
        goal_node = best_first_search(problem, lambda n: n.path_cost)
        assert goal_node is not None, "Should find a solution node"
        assert hasattr(goal_node, 'state'), "Return value should be a Node with a state attribute"
        path = solution(goal_node)
        assert path[0] == "A", f"Path should start at A, got {path[0]}"
        assert path[-1] == "F", f"Path should end at F, got {path[-1]}"
        results.append(TestResult("best_first_search finds path", True))
    except Exception as e:
        results.append(TestResult("best_first_search finds path", False, str(e)))

    # Test 2: Optimal path with uniform cost
    try:
        goal_node = best_first_search(problem, lambda n: n.path_cost)
        path = solution(goal_node)
        # Optimal: A -> D -> E -> F (cost = 4 + 1 + 1 = 6)
        # vs A -> B -> E -> F (cost = 1 + 3 + 1 = 5)
        assert path == ["A", "B", "E", "F"], f"Expected optimal path ['A','B','E','F'], got {path}"
        results.append(TestResult("best_first_search finds optimal path", True))
    except Exception as e:
        results.append(TestResult("best_first_search finds optimal path", False, str(e)))

    # Test 3: A* search (with heuristic)
    try:
        def f_astar(node):
            return node.path_cost + simple_heuristic(problem, node.state)

        goal_node = best_first_search(problem, f_astar)
        assert goal_node is not None, "A* should find a solution node"
        path = solution(goal_node)
        assert path[0] == "A", "Path should start at A"
        assert path[-1] == "F", "Path should end at F"
        results.append(TestResult("best_first_search with heuristic", True))
    except Exception as e:
        results.append(TestResult("best_first_search with heuristic", False, str(e)))

    # Test 4: Returns None when no path exists
    try:
        class IsolatedProblem(SimpleSearchProblem):
            def actions(self, state):
                if state == "A":
                    return []  # No way out of A
                return super().actions(state)

        isolated = IsolatedProblem()
        goal_node = best_first_search(isolated, lambda n: n.path_cost)
        assert goal_node is None, "Should return None when no path exists"
        results.append(TestResult("best_first_search returns None for no path", True))
    except Exception as e:
        results.append(TestResult("best_first_search returns None for no path", False, str(e)))

    # Test 5: Handles start = goal
    try:
        class SameStartGoal(SimpleSearchProblem):
            @property
            def goal(self):
                return "A"

        same = SameStartGoal()
        goal_node = best_first_search(same, lambda n: n.path_cost)
        assert goal_node is not None, "Should find a solution node when start=goal"
        path = solution(goal_node)
        assert path == ["A"], f"Expected ['A'] when start=goal, got {path}"
        results.append(TestResult("best_first_search handles start=goal", True))
    except Exception as e:
        results.append(TestResult("best_first_search handles start=goal", False, str(e)))

    return results


def run_all_tests(Node, expand, solution, insert, pop, best_first_search, verbose: bool = True) -> bool:
    """Run all tests and return True if all pass.

    Args:
        Node: The Node class implementation
        expand: The expand function implementation
        solution: The solution function implementation
        insert: The insert function implementation
        pop: The pop function implementation
        best_first_search: The best_first_search function implementation
        verbose: If True, print test results

    Returns:
        True if all tests pass, False otherwise
    """
    all_results = []

    if verbose:
        print("=" * 60)
        print("COMP 372 Lab 1 - Weighted A* Implementation Tests")
        print("=" * 60)

    # Test Node class
    if verbose:
        print("\n--- Node Class Tests ---")
    node_results = test_node(Node)
    all_results.extend(node_results)
    if verbose:
        for r in node_results:
            print(r)

    # Test expand
    if verbose:
        print("\n--- expand() Tests ---")
    expand_results = test_expand(Node, expand)
    all_results.extend(expand_results)
    if verbose:
        for r in expand_results:
            print(r)

    # Test solution
    if verbose:
        print("\n--- solution() Tests ---")
    solution_results = test_solution(Node, solution)
    all_results.extend(solution_results)
    if verbose:
        for r in solution_results:
            print(r)

    # Test insert and pop
    if verbose:
        print("\n--- insert() and pop() Tests ---")
    insert_pop_results = test_insert_and_pop(Node, insert, pop)
    all_results.extend(insert_pop_results)
    if verbose:
        for r in insert_pop_results:
            print(r)

    # Test best_first_search
    if verbose:
        print("\n--- best_first_search() Tests ---")
    bfs_results = test_best_first_search(Node, expand, solution, insert, pop, best_first_search)
    all_results.extend(bfs_results)
    if verbose:
        for r in bfs_results:
            print(r)

    # Summary
    passed = sum(1 for r in all_results if r.passed)
    total = len(all_results)
    all_pass = passed == total

    if verbose:
        print("\n" + "=" * 60)
        print(f"Results: {passed}/{total} tests passed")
        if all_pass:
            print("All tests PASSED!")
        else:
            print("Some tests FAILED. Review the output above.")
        print("=" * 60)

    return all_pass


def run_tests_on_solution() -> bool:
    """Run tests using the reference solution implementation.

    This is useful for verifying the tests themselves work correctly.

    Returns:
        True if all tests pass
    """
    from comp372_lab1_solution import Node, expand, solution, insert, pop, best_first_search

    return run_all_tests(Node, expand, solution, insert, pop, best_first_search)
