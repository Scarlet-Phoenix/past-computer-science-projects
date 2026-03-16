"""Geographic utilities: distance calculations, node lookups, and geocoding."""
from __future__ import annotations

import math
import random
from typing import Any, Tuple

import osmnx as ox
import networkx as nx


def haversine_m(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """Haversine distance in meters between two lat/lon points."""
    R = 6371000.0
    p1, p2 = math.radians(lat1), math.radians(lat2)
    dphi = math.radians(lat2 - lat1)
    dlmb = math.radians(lon2 - lon1)
    a = math.sin(dphi / 2) ** 2 + math.cos(p1) * math.cos(p2) * math.sin(dlmb / 2) ** 2
    return 2 * R * math.asin(math.sqrt(a))


def find_nearest_node(G: nx.MultiDiGraph, lat: float, lon: float) -> Tuple[Any, float]:
    """Find the nearest node in the graph to the given lat/lon coordinates.

    Uses OSMnx's spatial index for efficient lookup, with fallback to brute force.

    Args:
        G: The graph
        lat: Latitude
        lon: Longitude

    Returns:
        Tuple of (nearest_node, distance_in_meters)
    """
    try:
        # Try using OSMnx's native function (uses KD-tree spatial index)
        # Note: ox.nearest_nodes expects (X, Y) = (lon, lat)
        nearest_node = ox.nearest_nodes(G, lon, lat)
        node_lat = G.nodes[nearest_node]["y"]
        node_lon = G.nodes[nearest_node]["x"]
        distance = haversine_m(lat, lon, node_lat, node_lon)
        return nearest_node, distance
    except Exception:
        # Fallback to brute force search
        min_dist = math.inf
        nearest = None
        for node in G.nodes:
            node_lat = G.nodes[node]["y"]
            node_lon = G.nodes[node]["x"]
            dist = haversine_m(lat, lon, node_lat, node_lon)
            if dist < min_dist:
                min_dist = dist
                nearest = node
        return nearest, min_dist


def get_node_from_description(G: nx.MultiDiGraph, description: str) -> Tuple[Any, Tuple[float, float], float]:
    """Geocode a place description and return the nearest node in the graph.

    Args:
        G: The graph
        description: Place description to geocode

    Returns:
        Tuple of (nearest_node, (geocoded_lat, geocoded_lon), distance_to_node_meters)
    """
    lat, lon = ox.geocode(description)
    nearest_node, distance = find_nearest_node(G, lat, lon)
    return nearest_node, (lat, lon), distance


def pick_two_nodes_far_apart(G: nx.MultiDiGraph, seed: int = 7) -> Tuple[Any, Any]:
    """Pick two nodes that are reasonably far apart to make routing interesting."""
    rng = random.Random(seed)
    nodes = list(G.nodes)
    a = rng.choice(nodes)

    ay, ax = G.nodes[a]["y"], G.nodes[a]["x"]
    candidates = rng.sample(nodes, k=min(200, len(nodes)))
    b = max(candidates, key=lambda n: haversine_m(ay, ax, G.nodes[n]["y"], G.nodes[n]["x"]))
    return a, b