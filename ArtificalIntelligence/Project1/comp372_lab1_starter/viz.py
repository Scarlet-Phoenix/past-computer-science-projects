"""Visualization utilities for displaying routes on maps."""
from __future__ import annotations

from typing import TYPE_CHECKING, List, Any, Tuple

import folium

if TYPE_CHECKING:
    from .aima import SearchProblem


def get_edge_coordinates(G, u: Any, v: Any) -> List[Tuple[float, float]]:
    """Get the coordinates along an edge, using geometry if available.

    OSMnx edges may have a 'geometry' attribute containing the actual road shape.
    If not present, we just return the start and end node coordinates.

    Args:
        G: The graph
        u: Start node
        v: End node

    Returns:
        List of (lat, lon) tuples along the edge
    """
    u_coords = (G.nodes[u]["y"], G.nodes[u]["x"])
    v_coords = (G.nodes[v]["y"], G.nodes[v]["x"])

    # Get edge data - handle MultiDiGraph (may have multiple edges between nodes)
    edge_data = G.get_edge_data(u, v)
    if edge_data is None:
        # No edge exists, just return node coordinates
        return [u_coords, v_coords]

    # For MultiDiGraph, edge_data is a dict of {key: attributes}
    # Get the first edge's data
    if isinstance(edge_data, dict):
        # Get first key's attributes
        first_key = next(iter(edge_data))
        attrs = edge_data[first_key]
    else:
        attrs = edge_data

    # Check if edge has geometry
    if "geometry" in attrs:
        geom = attrs["geometry"]

        # Handle different geometry formats
        try:
            # If it's a Shapely geometry object
            if hasattr(geom, "coords"):
                coords = list(geom.coords)
                return [(lat, lon) for lon, lat in coords]

            # If it's a WKT string (from GraphML), parse it
            if isinstance(geom, str):
                from shapely import wkt
                geom_obj = wkt.loads(geom)
                coords = list(geom_obj.coords)
                return [(lat, lon) for lon, lat in coords]
        except Exception:
            # Fall back to simple node-to-node line
            pass

    # No geometry or couldn't parse it, just connect the nodes directly
    return [u_coords, v_coords]


def generate_map_with_route(problem: "SearchProblem", path: List[Any]) -> folium.Map:
    """Create a Folium map displaying the route from start to goal.

    Args:
        problem: The search problem containing the graph and start/goal states
        path: List of states representing the path from start to goal

    Returns:
        A Folium map with the route displayed
    """
    G = problem.graph
    start = problem.initial
    goal = problem.goal

    # Get node coordinates (where the route starts/ends)
    sy, sx = G.nodes[start]["y"], G.nodes[start]["x"]
    gy, gx = G.nodes[goal]["y"], G.nodes[goal]["x"]

    center = ((sy + gy) / 2, (sx + gx) / 2)

    # Create map with CartoDB tiles (often more reliable)
    m = folium.Map(
        location=center,
        zoom_start=14,
        tiles="OpenStreetMap",
    )

    # Build the route coordinates using edge geometry
    if path and len(path) >= 2:
        route_coords = []
        for i in range(len(path) - 1):
            u, v = path[i], path[i + 1]
            edge_coords = get_edge_coordinates(G, u, v)

            if not route_coords:
                # First edge - add all coordinates
                route_coords.extend(edge_coords)
            else:
                # Subsequent edges - skip the first point
                route_coords.extend(edge_coords[1:])

        folium.PolyLine(route_coords, color="blue", weight=5, opacity=0.85).add_to(m)

    # Add simple markers for start and goal
    folium.Marker(
        location=(sy, sx),
        popup="Start",
        icon=folium.Icon(color="green"),
    ).add_to(m)

    folium.Marker(
        location=(gy, gx),
        popup="Goal",
        icon=folium.Icon(color="red"),
    ).add_to(m)

    return m
