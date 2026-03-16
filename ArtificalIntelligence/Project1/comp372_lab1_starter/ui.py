"""Interactive UI components for exploring Weighted A* search."""
from __future__ import annotations

import time
from dataclasses import dataclass, field
from typing import Any, Callable, Dict, List, Optional

import ipywidgets as widgets
from IPython.display import display, clear_output

from .viz import generate_map_with_route
from .aima import SearchProblem


@dataclass
class SearchStats:
    """Tracks statistics during search execution."""
    nodes_expanded: int = 0
    nodes_generated: int = 0
    max_frontier_size: int = 0
    current_frontier_size: int = 0

    def reset(self) -> None:
        """Reset all statistics."""
        self.nodes_expanded = 0
        self.nodes_generated = 0
        self.max_frontier_size = 0
        self.current_frontier_size = 0

    def record_insert(self) -> None:
        """Record a node being added to the frontier."""
        self.current_frontier_size += 1
        self.max_frontier_size = max(self.max_frontier_size, self.current_frontier_size)

    def record_pop(self) -> None:
        """Record a node being removed from the frontier."""
        self.current_frontier_size -= 1

    def record_expand(self, num_children: int) -> None:
        """Record a node expansion."""
        self.nodes_expanded += 1
        self.nodes_generated += num_children


@dataclass
class SearchResult:
    """Results from running a search algorithm.

    Attributes:
        path: List of states from initial to goal, or None if no path found
        path_cost: Total cost of the path in meters, or None if no path
        num_nodes: Number of nodes (states) in the path
        execution_time: Time taken to find the solution in seconds
        heuristic_estimate: Initial heuristic estimate from start to goal
        nodes_expanded: Total number of nodes expanded during search
        nodes_generated: Total number of child nodes generated
        max_frontier_size: Maximum size of the frontier during search
    """
    path: Optional[List[Any]]
    path_cost: Optional[float]
    num_nodes: int
    execution_time: float
    heuristic_estimate: float
    nodes_expanded: int = 0
    nodes_generated: int = 0
    max_frontier_size: int = 0

    @property
    def found(self) -> bool:
        """True if a path was found."""
        return self.path is not None


def compute_path_cost(problem: SearchProblem, path: List[Any]) -> float:
    """Compute the total cost of a path.

    Args:
        problem: The search problem
        path: List of states from initial to goal

    Returns:
        Total path cost (sum of action costs)
    """
    if path is None or len(path) < 2:
        return 0.0

    total_cost = 0.0
    for i in range(len(path) - 1):
        s = path[i]
        s_prime = path[i + 1]
        total_cost += problem.action_cost(s, s_prime, s_prime)
    return total_cost


def create_tracked_functions(
    expand_fn: Callable,
    insert_fn: Callable,
    pop_fn: Callable,
    stats: SearchStats,
) -> tuple:
    """Create wrapped versions of search functions that track statistics.

    Args:
        expand_fn: The student's expand function
        insert_fn: The student's insert function
        pop_fn: The student's pop function
        stats: SearchStats object to record statistics

    Returns:
        Tuple of (tracked_expand, tracked_insert, tracked_pop)
    """
    def tracked_expand(problem, node):
        children = expand_fn(problem, node)
        if children:
            stats.record_expand(len(children))
        else:
            stats.record_expand(0)
        return children

    def tracked_insert(frontier, node, f, counter):
        result = insert_fn(frontier, node, f, counter)
        stats.record_insert()
        return result

    def tracked_pop(frontier):
        result = pop_fn(frontier)
        stats.record_pop()
        return result

    return tracked_expand, tracked_insert, tracked_pop


def create_tracked_best_first_search(
    Node,
    expand_fn: Callable,
    solution_fn: Callable,
    insert_fn: Callable,
    pop_fn: Callable,
    goal_test_fn: Callable,
    stats: SearchStats,
) -> Callable:
    """Create a best_first_search function with tracking wrappers.

    This creates a version of best_first_search that uses tracked versions
    of the student's functions to collect statistics.

    Args:
        Node: The student's Node class
        expand_fn: The student's expand function
        solution_fn: The student's solution function
        insert_fn: The student's insert function
        pop_fn: The student's pop function
        goal_test_fn: The goal_test function
        stats: SearchStats object to record statistics

    Returns:
        A best_first_search function that tracks statistics
    """
    tracked_expand, tracked_insert, tracked_pop = create_tracked_functions(
        expand_fn, insert_fn, pop_fn, stats
    )

    def tracked_best_first_search(problem: SearchProblem, f: Callable):
        node = Node(state=problem.initial)
        frontier = []
        counter = [0]
        tracked_insert(frontier, node, f, counter)

        reached: Dict[Any, Any] = {}
        reached[node.state] = node

        while frontier:
            node = tracked_pop(frontier)

            if goal_test_fn(problem, node.state):
                return node

            for child in tracked_expand(problem, node):
                s = child.state
                if s not in reached or child.path_cost < reached[s].path_cost:
                    reached[s] = child
                    tracked_insert(frontier, child, f, counter)

        return None

    return tracked_best_first_search


def run_search(
    problem: SearchProblem,
    weighted_a_star_fn: Callable[[SearchProblem, Callable, float], Optional[List[Any]]],
    heuristic: Callable[[SearchProblem, Any], float],
    w: float = 1.0,
) -> SearchResult:
    """Run weighted A* search and collect basic statistics.

    Note: This version does not track frontier/expansion statistics.
    Use run_search_with_stats for detailed statistics.

    Args:
        problem: The search problem to solve
        weighted_a_star_fn: The weighted A* function (problem, heuristic, w) -> path
        heuristic: The heuristic function to use
        w: Weight for the heuristic

    Returns:
        SearchResult containing path and statistics
    """
    h_initial = heuristic(problem, problem.initial)

    start_time = time.perf_counter()
    goal_node = weighted_a_star_fn(problem, heuristic, w)
    end_time = time.perf_counter()

    execution_time = end_time - start_time

    if goal_node is None:
        return SearchResult(
            path=None,
            path_cost=None,
            num_nodes=0,
            execution_time=execution_time,
            heuristic_estimate=h_initial,
        )

    # Reconstruct path from the solution node
    path = []
    current = goal_node
    while current is not None:
        path.append(current.state)
        current = current.parent
    path.reverse()

    path_cost = compute_path_cost(problem, path)

    return SearchResult(
        path=path,
        path_cost=path_cost,
        num_nodes=len(path),
        execution_time=execution_time,
        heuristic_estimate=h_initial,
    )


def run_search_with_stats(
    problem: SearchProblem,
    weighted_a_star_fn: Callable,
    heuristic: Callable[[SearchProblem, Any], float],
    w: float,
    *,
    Node=None,
    expand_fn: Callable = None,
    solution_fn: Callable = None,
    insert_fn: Callable = None,
    pop_fn: Callable = None,
    goal_test_fn: Callable = None,
) -> SearchResult:
    """Run weighted A* search and collect statistics.

    When the optional component functions (Node, expand_fn, etc.) are
    provided, a tracked version of best-first search is used internally
    to record detailed statistics (nodes expanded, generated, max frontier
    size).  Otherwise ``weighted_a_star_fn`` is called directly and only
    basic statistics (path cost, execution time) are collected.

    Args:
        problem: The search problem to solve
        weighted_a_star_fn: The weighted A* function (problem, heuristic, w) -> path
        heuristic: The heuristic function to use
        w: Weight for the heuristic
        Node: The student's Node class (optional, for detailed stats)
        expand_fn: The student's expand function (optional, for detailed stats)
        solution_fn: The student's solution function (optional, for detailed stats)
        insert_fn: The student's insert function (optional, for detailed stats)
        pop_fn: The student's pop function (optional, for detailed stats)
        goal_test_fn: The goal_test function (optional, for detailed stats)

    Returns:
        SearchResult containing path and statistics
    """
    use_detailed_stats = all(
        fn is not None
        for fn in [Node, expand_fn, solution_fn, insert_fn, pop_fn, goal_test_fn]
    )

    h_initial = heuristic(problem, problem.initial)

    if use_detailed_stats:
        stats = SearchStats()

        # Create tracked version of best_first_search
        tracked_bfs = create_tracked_best_first_search(
            Node, expand_fn, solution_fn, insert_fn, pop_fn, goal_test_fn, stats
        )

        # Create the f function for weighted A*
        def f(node) -> float:
            return node.path_cost + w * heuristic(problem, node.state)

        start_time = time.perf_counter()
        goal_node = tracked_bfs(problem, f)
        end_time = time.perf_counter()

        nodes_expanded = stats.nodes_expanded
        nodes_generated = stats.nodes_generated
        max_frontier_size = stats.max_frontier_size

        # Reconstruct path from solution node
        path = solution_fn(goal_node) if goal_node is not None else None
    else:
        start_time = time.perf_counter()
        goal_node = weighted_a_star_fn(problem, heuristic, w)
        end_time = time.perf_counter()

        nodes_expanded = 0
        nodes_generated = 0
        max_frontier_size = 0

        # Reconstruct path from solution node
        if goal_node is not None:
            path = []
            current = goal_node
            while current is not None:
                path.append(current.state)
                current = current.parent
            path.reverse()
        else:
            path = None

    execution_time = end_time - start_time

    if path is None:
        return SearchResult(
            path=None,
            path_cost=None,
            num_nodes=0,
            execution_time=execution_time,
            heuristic_estimate=h_initial,
            nodes_expanded=nodes_expanded,
            nodes_generated=nodes_generated,
            max_frontier_size=max_frontier_size,
        )

    path_cost = compute_path_cost(problem, path)

    return SearchResult(
        path=path,
        path_cost=path_cost,
        num_nodes=len(path),
        execution_time=execution_time,
        heuristic_estimate=h_initial,
        nodes_expanded=nodes_expanded,
        nodes_generated=nodes_generated,
        max_frontier_size=max_frontier_size,
    )


def print_search_result(result: SearchResult, w: float, place_name: str = "") -> None:
    """Print search results in a formatted way.

    Args:
        result: The search result to display
        w: The weight used for the search
        place_name: Optional place name to display
    """
    if place_name:
        print(f"Place: {place_name}")
    print(f"Weight (w): {w}")
    print("-" * 40)

    if not result.found:
        print("No path found.")
        print(f"Execution time: {result.execution_time:.4f} seconds")
        if result.nodes_expanded > 0:
            print(f"Nodes expanded: {result.nodes_expanded:,}")
            print(f"Max frontier size: {result.max_frontier_size:,}")
        return

    print(f"Path found!")
    print(f"  Nodes in path: {result.num_nodes}")
    print(f"  Path cost: {result.path_cost:,.1f} meters ({result.path_cost/1000:.2f} km)")
    print(f"  Heuristic estimate: {result.heuristic_estimate:,.1f} meters ({result.heuristic_estimate/1000:.2f} km)")

    # Calculate how much longer the actual path is vs straight-line
    if result.heuristic_estimate > 0:
        path_ratio = result.path_cost / result.heuristic_estimate
        print(f"  Path/Heuristic ratio: {path_ratio:.2f}x")

    # Show search statistics if available
    if result.nodes_expanded > 0:
        print(f"  Nodes expanded: {result.nodes_expanded:,}")
        print(f"  Nodes generated: {result.nodes_generated:,}")
        print(f"  Max frontier size: {result.max_frontier_size:,}")

    print(f"  Execution time: {result.execution_time:.4f} seconds")


def interactive_weighted_a_star(
    problem: SearchProblem,
    heuristic: Callable[[SearchProblem, Any], float],
    place_name: str = "",
    *,
    Node=None,
    expand_fn: Callable = None,
    solution_fn: Callable = None,
    insert_fn: Callable = None,
    pop_fn: Callable = None,
    goal_test_fn: Callable = None,
    weighted_a_star_fn: Callable = None,
) -> None:
    """Display an interactive UI for exploring Weighted A* with different weights.

    Creates a slider to adjust the weight w and a button to run the search.
    Results are displayed as an interactive Folium map with statistics.

    There are two ways to call this function:

    1. With detailed stats (recommended): Pass the student's functions
       interactive_weighted_a_star(problem, heuristic, place_name,
           Node=Node, expand_fn=expand, solution_fn=solution,
           insert_fn=insert, pop_fn=pop, goal_test_fn=goal_test)

    2. Simple mode: Pass weighted_a_star_fn (no detailed stats)
       interactive_weighted_a_star(problem, heuristic, place_name,
           weighted_a_star_fn=weighted_a_star)

    Args:
        problem: The search problem to solve
        heuristic: The heuristic function to use
        place_name: Optional name to display (e.g., city name)
        Node: The student's Node class (for detailed stats)
        expand_fn: The student's expand function (for detailed stats)
        solution_fn: The student's solution function (for detailed stats)
        insert_fn: The student's insert function (for detailed stats)
        pop_fn: The student's pop function (for detailed stats)
        goal_test_fn: The goal_test function (for detailed stats)
        weighted_a_star_fn: The weighted A* function (simple mode, no detailed stats)
    """
    use_detailed_stats = all([
        Node is not None,
        expand_fn is not None,
        solution_fn is not None,
        insert_fn is not None,
        pop_fn is not None,
        goal_test_fn is not None,
    ])

    if not use_detailed_stats and weighted_a_star_fn is None:
        raise ValueError(
            "Either provide all student functions (Node, expand_fn, solution_fn, "
            "insert_fn, pop_fn, goal_test_fn) for detailed stats, or provide "
            "weighted_a_star_fn for simple mode."
        )

    w_slider = widgets.FloatSlider(
        value=1.0,
        min=1.0,
        max=8.0,
        step=0.25,
        description="w",
        continuous_update=False,
    )
    run_btn = widgets.Button(description="Run Weighted A*", button_style="success")
    out = widgets.Output()

    def on_run_search(_):
        with out:
            clear_output()
            w = float(w_slider.value)

            if use_detailed_stats:
                result = run_search_with_stats(
                    problem, weighted_a_star_fn, heuristic, w,
                    Node=Node, expand_fn=expand_fn, solution_fn=solution_fn,
                    insert_fn=insert_fn, pop_fn=pop_fn, goal_test_fn=goal_test_fn,
                )
            else:
                result = run_search_with_stats(
                    problem, weighted_a_star_fn, heuristic, w,
                )

            print_search_result(result, w, place_name)

            if result.found:
                print()
                display(generate_map_with_route(problem, result.path))

    run_btn.on_click(on_run_search)

    display(widgets.HBox([w_slider, run_btn]))
    display(out)

    # Auto-run once so something shows immediately
    on_run_search(None)
