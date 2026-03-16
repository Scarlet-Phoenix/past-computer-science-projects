"""Graph loading and caching utilities."""
from __future__ import annotations

import os
import re

import osmnx as ox
import networkx as nx


def slugify(s: str) -> str:
    """Convert a string to a safe filename slug."""
    s = s.strip().lower()
    s = re.sub(r"[^a-z0-9]+", "_", s)
    return re.sub(r"_+", "_", s).strip("_")


def graph_cache_path(place: str, network_type: str, dist_m: int, cache_dir: str = "./osmnx_cache_graphs") -> str:
    """Return the cache file path for a given place/network/distance."""
    return os.path.join(cache_dir, f"{slugify(place)}__{network_type}__{dist_m}m.graphml")


def load_city_graph(
    place: str,
    network_type: str = "walk",
    dist_m: int = 4000,
    cache_dir: str = "./osmnx_cache_graphs"
) -> nx.MultiDiGraph:
    """Load a city graph from disk if cached; otherwise download from OpenStreetMap via OSMnx.

    Uses a buffer around the geocoded center of `place`.
    The graph includes edge geometries for accurate route visualization.
    """
    os.makedirs(cache_dir, exist_ok=True)
    path = graph_cache_path(place, network_type, dist_m, cache_dir)

    if os.path.exists(path):
        G = ox.load_graphml(path)
        # Ensure edge geometries are present (they should be restored from WKT)
        # If not, add them back
        if not _has_edge_geometries(G):
            G = ox.add_edge_bearings(G)
        return G

    center_point = ox.geocode(place)  # (lat, lon)
    G = ox.graph_from_point(center_point, dist=dist_m, network_type=network_type, simplify=True)
    G = ox.distance.add_edge_lengths(G)
    ox.save_graphml(G, path)
    return G


def _has_edge_geometries(G: nx.MultiDiGraph) -> bool:
    """Check if the graph has edge geometry attributes."""
    for u, v, data in G.edges(data=True):
        if "geometry" in data:
            return True
        # Only check a few edges
        break
    return False
