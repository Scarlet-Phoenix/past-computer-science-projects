# Lab 1: Weighted A* Search on Real City Maps

## Problem Setup

In this lab you will implement the **Weighted A\* search algorithm** in the provided Python notebook (`lab1.ipynb`) and apply it to solving search problems using real city street map data. The starter package downloads actual road networks from [OpenStreetMap](https://www.openstreetmap.org/), and your algorithm will find routes between REAL locations in cities like Memphis!

Your task is to implement the core data structures and functions that make up a best-first search framework, then use them to build a complete Weighted A\* solver. Once your implementation is working, you will use it to compare three search algorithms:

1. Uniform-Cost Search
2. A\*, and
3. Greedy Best-First Search.

## Background: Weighted A* and Best-First Search

### Best-First Search

Best-first search is a general graph-search algorithm that selects which node to expand next based on an **evaluation function** f(n). The algorithm maintains a **frontier** (priority queue) of nodes ordered by their f-value, always expanding the node with the lowest value first. It also maintains a **reached** map to avoid re-exploring states unless a cheaper path is found. The pseudocode (from *Artificial Intelligence: A Modern Approach*) is:

```
function BEST-FIRST-SEARCH(problem, f) returns a solution node or failure
    node ← NODE(STATE = problem.INITIAL)
    frontier ← a priority queue ordered by f, with node as an element
    reached ← a lookup table, with one entry with key problem.INITIAL and value node
    while not IS-EMPTY(frontier) do
        node ← POP(frontier)
        if problem.IS-GOAL(node.STATE) then return node
        for each child in EXPAND(problem, node) do
            s ← child.STATE
            if s is not in reached or child.PATH-COST < reached[s].PATH-COST then
                reached[s] ← child
                add child to frontier
    return failure

function EXPAND(problem, node) yields nodes
    s ← node.STATE
    for each action in problem.ACTIONS(s) do
        s' ← problem.RESULT(s, action)
        cost ← node.PATH-COST + problem.ACTION-COST(s, action, s')
        yield NODE(STATE = s', PARENT = node, ACTION = action, PATH-COST = cost)
```

The power of best-first search lies in its generality: by choosing different evaluation functions f(n), we obtain different search algorithms.

### A* Search

A\* search uses the evaluation function:

**f(n) = g(n) + h(n)**

where **g(n)** is the actual path cost from the initial state to node n, and **h(n)** is a heuristic function that estimates the cost from n to the goal. When h(n) is **admissible** (never overestimates the true cost), A\* is guaranteed to find an optimal solution.

### Weighted A*

Weighted A\* introduces a weight parameter **w** to control the influence of the heuristic:

**f(n) = g(n) + w * h(n)**

The weight creates a spectrum of behaviors:

| Weight | Evaluation Function | Algorithm | Behavior |
|---|---|---|---|
| w = 0 | f(n) = g(n) | Uniform-Cost Search | Optimal but slow; ignores heuristic entirely |
| w = 1 | f(n) = g(n) + h(n) | A\* Search | Optimal (with admissible h); balances cost and estimate |
| w > 1 | f(n) = g(n) + w*h(n) | Weighted A\* | Trades optimality for speed; finds solutions faster |
| w very large | f(n) ≈ h(n) | Greedy Best-First | Fast but may find poor paths; path cost nearly ignored |

By increasing w, the algorithm becomes greedier — it focuses more on moving toward the goal quickly and less on finding the cheapest path. The solution found by Weighted A\* with weight w is guaranteed to cost no more than w times the optimal cost (w-admissible).

### Heuristics

A **heuristic function** h(n) provides an estimate of the remaining cost from a state to the goal. A good heuristic guides the search toward the goal efficiently, reducing the number of nodes that need to be expanded.

A heuristic is **admissible** if it never overestimates the true cost to reach the goal. Admissibility is important because it guarantees that A\* (with w = 1) will find an optimal solution.

### The Straight-Line Distance Heuristic

This lab provides an admissible heuristic called `heuristic_sld` that computes the **straight-line distance** (also known as great-circle distance) from the current state to the goal state.

Because the map is defined on the surface of the Earth, straight-line distance is computed using the **Haversine formula**, which calculates the shortest distance between two points on a sphere given their latitude and longitude coordinates. The Haversine formula accounts for the curvature of the Earth, producing an accurate distance in meters.

This heuristic is admissible because no path along streets can be shorter than the straight-line distance between two points — you cannot travel a shorter distance than a straight line. This makes it a safe lower bound for A\* search.

### Implementation Notes: Data Structures

Best-first search relies on two key data structures: a **priority queue** for the frontier and a **lookup table** for the reached states.

#### Frontier (Priority Queue)

The frontier is a priority queue that always returns the node with the lowest f-value. Python's [`heapq`](https://docs.python.org/3/library/heapq.html) module provides an efficient min-heap implementation. The two main operations are:

- `heapq.heappush(heap, item)` — add an item to the heap
- `heapq.heappop(heap)` — remove and return the smallest item

The heap maintains its elements as a list sorted by the first element of each tuple. A typical pattern is to push tuples of `(priority, item)` so that items are ordered by their priority value.

**The tiebreaking problem:** When two nodes have the same f-value, Python's `heapq` will attempt to compare the second element of the tuple to break the tie. However, `Node` objects do not support comparison (there is no natural ordering on search nodes), so this will raise a `TypeError`. To solve this, you should include a **tiebreaker** as a middle element in the tuple: `(f_value, tiebreaker, node)`. The `insert` function receives a `counter` parameter (a mutable list containing a single integer, e.g. `[0]`) for this purpose. Each time a node is inserted, use the current counter value as the tiebreaker and then increment it. Because the counter increases monotonically, nodes with equal f-values will be ordered by insertion time (FIFO), and Python will never need to compare `Node` objects directly.

#### Reached Table (Dictionary)

The reached table can be implemented as a Python `dict` that maps each visited state to the `Node` that reached it with the lowest path cost. This serves two purposes: it prevents the algorithm from re-exploring states it has already visited, and it allows the algorithm to update a state's entry when a cheaper path is discovered. A simple `dict` provides O(1) average-case lookup and insertion, which is exactly what the algorithm needs.

## Setup and Installation

### 1. Install Python

This lab requires **Python 3.10 or later**. Check your installed version by running:

```bash
python3 --version
```

If Python is not installed or is an older version, follow the instructions for your operating system:

**macOS:**

```bash
brew install python
```

If you do not have Homebrew, install it first from [https://brew.sh](https://brew.sh), or download Python directly from [https://www.python.org/downloads/](https://www.python.org/downloads/).

**Windows:**

Download and run the installer from [https://www.python.org/downloads/](https://www.python.org/downloads/). During installation, **check the box that says "Add Python to PATH"**.

After installation, open a new Command Prompt or PowerShell and verify with:

```cmd
python --version
```

**Linux (Ubuntu/Debian):**

```bash
sudo apt update
sudo apt install python3 python3-venv python3-pip
```

### 2. Create a Virtual Environment

Navigate to the project directory and create a virtual environment:

**macOS / Linux:**

```bash
cd path/to/lab1-weighted-a-star
python3 -m venv venv
```

**Windows:**

```cmd
cd path\to\lab1-weighted-a-star
python -m venv venv
```

### 3. Activate the Virtual Environment

You must activate the virtual environment each time you open a new terminal session.

**macOS / Linux:**

```bash
source venv/bin/activate
```

**Windows (Command Prompt):**

```cmd
venv\Scripts\activate.bat
```

**Windows (PowerShell):**

```powershell
venv\Scripts\Activate.ps1
```

When activated, you should see `(venv)` at the beginning of your terminal prompt.

### 4. Install Jupyter Notebook

With the virtual environment activated, install Jupyter:

```bash
pip install jupyter
```

## Running the Notebook

1. **Activate the virtual environment** (if not already activated — see above).

2. **Launch Jupyter Notebook:**

   ```bash
   jupyter notebook lab1.ipynb
   ```

   This will open the notebook in your default web browser.

3. **Run the first cell** to install dependencies (`osmnx`, `folium`, `ipywidgets`, `matplotlib`). This only needs to happen once.

4. **Run the second and third cells** to configure the search problem and load the map data. There is where you can set the initial and goal states. The first run may take several minutes as it downloads and caches the street network from OpenStreetMap. Subsequent runs will load from the cache and be much faster.

5. **Implement the TODO functions** in order, running the provided test cells after each one as a sanity check on your implementation. (These unit tests do not guarantee correctness!)

6. **Run the baseline search** and verify your results using the map plot and turn-by-turn directions.

## Grading Rubric

| Component | Weight | Description |
|---|---|---|
| **Implementation** | 80% | Correct implementation of Weighted A\* and all supporting functions and classes: `Node`, `expand`, `solution`, `insert`, `pop`, `best_first_search`, and `weighted_a_star`. All provided unit tests must pass. |
| **Algorithm Comparison** | 20% | Comparison of UCS, A\*, and Greedy Best-First Search in terms of solution path cost, nodes expanded, and execution time. |

## Testing

Unit tests are provided in the notebook to help you verify each function as you implement it. These tests run on a small synthetic graph and check basic correctness. **However, passing the unit tests alone is not sufficient.** You should perform your own testing by trying multiple start and goal locations to confirm that your algorithm finds reasonable paths on the real city map.

Use the **route map** and **turn-by-turn directions** output to verify that your algorithm produces correct results. The plotted route should follow actual streets, and the directions should describe a sensible path between the two locations.

Try the following example runs by changing `START_DESCRIPTION` and `GOAL_DESCRIPTION` in the notebook:

1. **Rhodes College, Memphis, TN** to **Crosstown Concourse, Memphis, TN**
2. **FedEx Forum, Memphis, TN** to **Shelby Farms, Memphis, TN**
3. **Cooper-Young, Memphis, TN** to **Beale St., Memphis, TN**
4. **National Civil Rights Museum, Memphis, TN** to **Sun Studio, Memphis, TN**

For each run, check that:
- A path is found successfully
- The route on the map follows real streets and appears reasonable
- The turn-by-turn directions make geographic sense
- The path cost is plausible given the distance between the two locations

## Submission

Submit your completed solution code in `lab1.ipynb` to the GitHub for this assignment. Ensure that all cells have been run and that their output is visible in the notebook before submitting.
