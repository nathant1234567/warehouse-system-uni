# Warehouse Management System for 1st year assignment

## Overview

This project implements a simple Warehouse Management System for my second COMP5000 assessment. It is a Java application that uses SQLite databases to manage and visualize key business entities including customers, orders, deliveries, and inventory parts. The project demonstrates database integration, object-oriented design, and automated reporting through HTML interfaces.

## Features

* **SQLite Integration** — Uses embedded SQLite databases (`originaldata-2025.sqlite`, `warehousedata.sqlite`) to manage persistent data.
* **Entity Management** — Supports key entities such as:

    * Customers
    * Orders and Order Items
    * Deliveries
    * Parts and Batches
    * Purchase Orders
* **HTML Output** — Automatically generates human-readable HTML summaries in the `html/` directory 
* Includes JUnit-based tests to validate core functionality.

## Setup & Running

### Steps

1. **Clone the repository**

   ```bash
   git clone https://github.com/nathant1234567/warehouse-system-uni.git
   cd warehouse-system-uni/A2-outline
   ```
2. **Open the project**.
3. **Run the program** with:

   ```bash
   javac -cp "lib/*" -d out src/*.java
   java -cp "out:lib/*" MainClassName
   ```

