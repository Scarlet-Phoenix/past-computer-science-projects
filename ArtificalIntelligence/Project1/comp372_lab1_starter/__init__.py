"""COMP 372 Lab 1 Starter Package - Map loading, geocoding, and visualization utilities."""

from .graph import load_city_graph, graph_cache_path, slugify
from .geo import haversine_m, find_nearest_node, get_node_from_description, pick_two_nodes_far_apart
from .viz import generate_map_with_route
from .directions import generate_directions
from .ui import (
    interactive_weighted_a_star,
    run_search,
    run_search_with_stats,
    print_search_result,
    compute_path_cost,
    SearchResult,
)
from .aima import SearchProblem, LocationNotFoundError, goal_test, heuristic_sld
from .tests import (
    run_all_tests,
    run_tests_on_solution,
    test_node,
    test_expand,
    test_solution,
    test_insert_and_pop,
    test_best_first_search,
    SimpleSearchProblem,
)

__all__ = [
    # Graph loading
    "load_city_graph",
    "graph_cache_path",
    "slugify",
    # Geographic utilities
    "haversine_m",
    "find_nearest_node",
    "get_node_from_description",
    "pick_two_nodes_far_apart",
    # Visualization and UI
    "generate_map_with_route",
    "generate_directions",
    "interactive_weighted_a_star",
    "run_search",
    "run_search_with_stats",
    "print_search_result",
    "compute_path_cost",
    "SearchResult",
    # Search problem wrapper and functions
    "SearchProblem",
    "LocationNotFoundError",
    "goal_test",
    "heuristic_sld",
    # Testing utilities
    "run_all_tests",
    "run_tests_on_solution",
    "test_node",
    "test_expand",
    "test_solution",
    "test_insert_and_pop",
    "test_best_first_search",
    "SimpleSearchProblem",
]
