"""Turn-by-turn direction generation from a route on an OSMnx street graph."""
from __future__ import annotations

import ast
import math
from typing import TYPE_CHECKING, Any, Dict, List

if TYPE_CHECKING:
    from .aima import SearchProblem


# ---------------------------------------------------------------------------
# Private helpers
# ---------------------------------------------------------------------------

def _get_edge_name(G, u: Any, v: Any) -> str:
    """Extract the street name for the edge (u, v).

    Handles missing names, list-valued names, and stringified lists that
    result from GraphML round-tripping.
    """
    edge_data = G.get_edge_data(u, v)
    if edge_data is None:
        return "unnamed road"
    attrs = next(iter(edge_data.values()))
    name = attrs.get("name")
    if name is None:
        highway = attrs.get("highway", "road")
        if isinstance(highway, list):
            highway = highway[0]
        return f"unnamed {highway}"
    if isinstance(name, list):
        return name[0]
    # GraphML may serialise lists as their repr string
    if isinstance(name, str) and name.startswith("["):
        try:
            parsed = ast.literal_eval(name)
            if isinstance(parsed, list) and parsed:
                return parsed[0]
        except (ValueError, SyntaxError):
            pass
    return str(name)


def _get_edge_length(G, u: Any, v: Any) -> float:
    """Return the minimum edge length in metres between *u* and *v*."""
    edge_data = G.get_edge_data(u, v)
    if edge_data is None:
        return 0.0
    return min(float(attr.get("length", 0.0)) for attr in edge_data.values())


def _compute_bearing(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """Return the initial compass bearing (0-360) from point 1 to point 2."""
    lat1, lon1, lat2, lon2 = map(math.radians, [lat1, lon1, lat2, lon2])
    dlon = lon2 - lon1
    x = math.sin(dlon) * math.cos(lat2)
    y = (math.cos(lat1) * math.sin(lat2)
         - math.sin(lat1) * math.cos(lat2) * math.cos(dlon))
    return math.degrees(math.atan2(x, y)) % 360


def _bearing_to_compass(bearing: float) -> str:
    """Convert a bearing (0-360) to an 8-point compass direction."""
    directions = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"]
    return directions[round(bearing / 45) % 8]


def _classify_turn(bearing_before: float, bearing_after: float) -> str:
    """Classify the turn between two consecutive bearings."""
    delta = (bearing_after - bearing_before + 180) % 360 - 180
    if abs(delta) <= 20:
        return "Continue straight"
    if -60 <= delta < -20:
        return "Bear left"
    if -135 <= delta < -60:
        return "Turn left"
    if delta < -135:
        return "Make a U-turn"
    if 20 < delta <= 60:
        return "Bear right"
    if 60 < delta <= 135:
        return "Turn right"
    return "Make a U-turn"


def _format_distance(meters: float) -> str:
    """Format a distance for display."""
    if meters >= 1000:
        return f"{meters / 1000:.1f} km"
    return f"{meters:.0f} m"


# ---------------------------------------------------------------------------
# Public API
# ---------------------------------------------------------------------------

def generate_directions(problem: "SearchProblem", path: List[Any]) -> None:
    """Print turn-by-turn directions for a route.

    Walks the path edges, extracts street names, merges consecutive edges on
    the same street, computes compass bearings and turn directions, and prints
    a numbered list of human-readable directions.

    Args:
        problem: The search problem containing the graph.
        path: List of node IDs from initial to goal.
    """
    if path is None or len(path) < 2:
        print("No path to generate directions for.")
        return

    G = problem.graph

    # Step 1 – build raw edge list
    edges: List[Dict] = []
    for i in range(len(path) - 1):
        u, v = path[i], path[i + 1]
        lat1, lon1 = G.nodes[u]["y"], G.nodes[u]["x"]
        lat2, lon2 = G.nodes[v]["y"], G.nodes[v]["x"]
        edges.append({
            "name": _get_edge_name(G, u, v),
            "length": _get_edge_length(G, u, v),
            "bearing": _compute_bearing(lat1, lon1, lat2, lon2),
        })

    # Step 2 – merge consecutive same-street edges into segments
    segments: List[Dict] = []
    for edge in edges:
        if segments and segments[-1]["name"] == edge["name"]:
            segments[-1]["total_length"] += edge["length"]
            segments[-1]["exit_bearing"] = edge["bearing"]
        else:
            segments.append({
                "name": edge["name"],
                "total_length": edge["length"],
                "entry_bearing": edge["bearing"],
                "exit_bearing": edge["bearing"],
            })

    # Step 3 – print directions
    total_distance = sum(seg["total_length"] for seg in segments)

    print("\nTurn-by-Turn Directions")
    print("=" * 50)

    for i, seg in enumerate(segments):
        dist_str = _format_distance(seg["total_length"])
        if i == 0:
            compass = _bearing_to_compass(seg["entry_bearing"])
            instruction = f"Head {compass} on {seg['name']}"
        else:
            prev = segments[i - 1]
            turn = _classify_turn(prev["exit_bearing"], seg["entry_bearing"])
            instruction = f"{turn} onto {seg['name']}"
        print(f"{i + 1:>3}. {instruction:<55} ({dist_str})")

    print(f"{len(segments) + 1:>3}. Arrive at destination.")
    print("-" * 50)
    print(f"Total distance: {_format_distance(total_distance)} "
          f"({len(path) - 1} edges, {len(segments)} steps)")
