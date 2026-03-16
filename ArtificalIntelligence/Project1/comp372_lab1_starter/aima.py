"""Search problem wrapper for AIMA-style graph search.

This module provides a SearchProblem class that hides implementation details,
allowing students to focus on implementing the search algorithm.
"""
from __future__ import annotations

from typing import Any, List, Tuple

import networkx as nx

from .geo import haversine_m, get_node_from_description
from .graph import load_city_graph


class LocationNotFoundError(Exception):
    """Raised when a location description cannot be found on the map."""
    pass


class SearchProblem:
    """A search problem defined on a street map.

    This class loads the map and finds the start/goal locations automatically
    from place descriptions.

    Attributes:
        initial: The initial state
        goal: The goal state

    Methods:
        get_coordinates(state): Returns (lat, lon) for heuristic calculations
        actions(state): Returns list of available actions from state
        result(state, action): Returns the resulting state after taking action
        action_cost(s, action, s_prime): Returns the cost of the action

    Raises:
        LocationNotFoundError: If start or goal description cannot be found
    """

    def __init__(
        self,
        start_description: str,
        goal_description: str,
        place: str = "Memphis, Tennessee, USA",
        network_type: str = "walk",
        dist_meters: int = 4000,
    ):
        """Initialize a search problem from location descriptions.

        Args:
            start_description: Description of the starting location (e.g., "FedExForum, Memphis, TN")
            goal_description: Description of the goal location (e.g., "Memphis Zoo, Memphis, TN")
            place: City/region to load the map for
            network_type: Type of network ("walk", "drive", "bike")
            dist_meters: Radius around place center in meters

        Raises:
            LocationNotFoundError: If start or goal cannot be found on the map
        """
        self._graph = load_city_graph(place, network_type, dist_meters)

        # Print graph info for debugging
        print(f"Graph loaded: {len(self._graph.nodes):,} nodes, {len(self._graph.edges):,} edges")

        # Geocode start location
        try:
            self._initial, start_geocoded, start_dist = get_node_from_description(
                self._graph, start_description
            )
            self._start_geocoded = start_geocoded
            self._start_snap_distance = start_dist
        except Exception as e:
            raise LocationNotFoundError(f"Could not find start location: {start_description}") from e

        # Geocode goal location
        try:
            self._goal, goal_geocoded, goal_dist = get_node_from_description(
                self._graph, goal_description
            )
            self._goal_geocoded = goal_geocoded
            self._goal_snap_distance = goal_dist
        except Exception as e:
            raise LocationNotFoundError(f"Could not find goal location: {goal_description}") from e

        # Get the actual node coordinates
        start_node_coords = self.get_coordinates(self._initial)
        goal_node_coords = self.get_coordinates(self._goal)

        # Print coordinate information for debugging
        print(f"\nStart: {start_description}")
        print(f"  Geocoded location: ({start_geocoded[0]:.6f}, {start_geocoded[1]:.6f})")
        print(f"  Nearest graph node: ({start_node_coords[0]:.6f}, {start_node_coords[1]:.6f})")
        print(f"  Distance to node: {start_dist:.1f} meters")

        print(f"\nGoal: {goal_description}")
        print(f"  Geocoded location: ({goal_geocoded[0]:.6f}, {goal_geocoded[1]:.6f})")
        print(f"  Nearest graph node: ({goal_node_coords[0]:.6f}, {goal_node_coords[1]:.6f})")
        print(f"  Distance to node: {goal_dist:.1f} meters")
        print()

    @property
    def initial(self) -> Any:
        """The initial state."""
        return self._initial

    @property
    def goal(self) -> Any:
        """The goal state."""
        return self._goal

    @property
    def graph(self):
        """The underlying graph (for visualization purposes)."""
        return self._graph

    @property
    def start_geocoded(self) -> Tuple[float, float]:
        """The geocoded (lat, lon) of the start description."""
        return self._start_geocoded

    @property
    def goal_geocoded(self) -> Tuple[float, float]:
        """The geocoded (lat, lon) of the goal description."""
        return self._goal_geocoded

    def get_coordinates(self, state: Any) -> Tuple[float, float]:
        """Return the (latitude, longitude) coordinates of a state.

        Use this for computing heuristic distances.

        Args:
            state: A state in the search problem

        Returns:
            Tuple of (latitude, longitude) in degrees
        """
        return (self._graph.nodes[state]["y"], self._graph.nodes[state]["x"])

    def actions(self, state: Any) -> List[Any]:
        """Return the list of actions available from this state.

        Each action represents moving to an adjacent location.

        Args:
            state: The current state

        Returns:
            List of actions available from this state
        """
        return list(self._graph.neighbors(state))

    def result(self, state: Any, action: Any) -> Any:
        """Return the state that results from executing action in state.

        Args:
            state: The current state
            action: The action to take

        Returns:
            The resulting state
        """
        return action

    def action_cost(self, s: Any, action: Any, s_prime: Any) -> float:
        """Return the cost of taking action to go from state s to state s_prime.

        In a street map, this is the distance of the road segment in meters.

        Args:
            s: The starting state
            action: The action taken
            s_prime: The resulting state

        Returns:
            The cost (distance in meters) of the action
        """
        data = self._graph.get_edge_data(s, s_prime)
        if data is None:
            return float('inf')
        return min(attr.get("length", 1.0) for attr in data.values())


def goal_test(problem: SearchProblem, state: Any) -> bool:
    """Return True if state is the goal state.

    Args:
        problem: The search problem
        state: The state to test

    Returns:
        True if state equals the goal state
    """
    return state == problem.goal


def heuristic_sld(problem: SearchProblem, state: Any) -> float:
    """Compute the straight-line distance heuristic h(state) for A* search.

    Returns the great-circle distance from state to goal in meters.
    This is admissible because you cannot travel shorter than a straight line.

    Args:
        problem: The search problem
        state: The state to evaluate

    Returns:
        Estimated distance to goal in meters
    """
    lat1, lon1 = problem.get_coordinates(state)
    lat2, lon2 = problem.get_coordinates(problem.goal)
    return haversine_m(lat1, lon1, lat2, lon2)
