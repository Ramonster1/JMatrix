# JMatrix: A Java library for Matrix calculations

Welcome to the **JMatrix**. This Java project provides the required functionality for performing machine learning calculations using matrices such as calculating the dot product of matrices, transposition, applying a function to combine two matrices etc.

The library uses generic classes to allow for easy extension with minimal additional code. While primitive types and arrays generally provide better performance for matrix calculations—both in terms of latency and memory usage—object classes like BigDecimal can offer superior precision (e.g., BigDouble versus double). Additionally, using objects and lists with generics opens up possibilities for leveraging high-performance arithmetic libraries or developing a new low/no-GC arithmetic library using memory-mapped files, bridging the gap between performance and high-precision.

The goal of JMatrix is to provide a high-performance matrix library required for machine-learning. The current implementation of JMatrix provides a good foundation for further performance improvements. The current implementation offers flexible multithreading support for CPU intensive tasks such as calculating the dot product of two matrices, and applying a combine function on two matrices with same dimensions, such as addition and subtraction. Based on JMH benchmarking tests, this improves the performance of calculating
- the dot product by roughly 400%
- applying a combine function by roughly 300%

However, there's potential for more performance improvements, as discussed below.
This library is largely based on the Python code to make a neural network in the book [Make your own Neural Network by Tariq Rashid](https://howtolearnmachinelearning.com/books/machine-learning-books/make-your-own-neural-network/) which uses the Python numpy library for performing matrix calculations.

---

## Planned improvements/Roadmap towards low-latency

Roadmap towards low-latency is:
- Reduce latency of list calculations, e.g. set ArrayList capacity to prevent the underlying array from resizing.
- Use distributed computing (perhaps using Aeron cluster), to take a divide-and-conquer approach. Particularly beneficial when using large datasets often used in machine learning.
- Bytecode analysis for compiler improvements, such as keeping method bytecode under 35 bytes to improve C1 inlining optimisations
- Use a high-performance arithmetic library such as [Apfloat](https://www.apfloat.org/apfloat_java/) (number implementation classes such as BigDecimal are quite poor on GC performance since they're immutable, this is a big bottleneck in performance), or create a new low/no-GC high-precision low-latency arithmetic Java library. 
- Investigate whether using memory-mapped files could be used effectively to store large matrices to reduce the GC overhead
- Support custom thread pool management for improved multithreading control.
- More unit & benchmark tests. May remove fork/join implementations if they don't provide performance benefits since it requires more thought for the user to select an appropriate threshold value, or alternatively auto-generate a threshold value, similarly to parallel stream methods.

---

## Features

- **Generic Matrix Class**:
    - Handles any Java object type (`T`) as matrix elements.
    - Provides utility methods for initialization, validation, and operations like matrix appending.
    - Ensures integrity through exception handling for incompatible operations.

- **Matrix Calculations**:
    - Supports essential matrix operations like addition, subtraction, multiplication, and dot products through dedicated calculators (`MatrixCalculator` for single matrix calculations and `MatricesCalculator` for calculations on multiple matrices).
    - Focus on code scalability and reuse with clear abstractions.

- **Multithreading Support**:
    - Incorporates multithreaded computational capabilities, improving performance for large matrices.
    - 3 different methods are provided for the multithreaded methods.
    - 1. parallel...(): Applies an algorithm to fully parallelise the operation evenly using all available processors
    - 2. parallel...(..., int nThreads): Parallelism is based on nThreads provided
    - 3. forkJoin...(..., int computationsPerTaskThreshold): Uses the Fork/Join framework to perform the operation concurrently. The threshold value is the number of mathematical computations per Fork/Join task.

- **Test Coverage**:
    - Includes JUnit test cases to verify correct functionality for matrix operations, including serial and parallel computations.
    - Includes JHM benchmark tests and results.

---

## Classes Overview

### 1. **`Matrix<T>`**
The core class representing a two-dimensional matrix. There is no upper bound specified to allow new Maths libraries to be used e.g. Apfloat (and the Number class doesn't define anything useful such as an addition/subtraction method).

- **Features**:
    - Generic implementation to support matrices of any type.
    - Initialization via precomputed lists or random value generation.
    - Equality checks (`equals`, `hashCode`) for comparing matrices.
    - Matrix operation helpers like `appendMatrix`.

- **Constructor Examples**:
  ```java
  // Create a 100x100 matrix where each row has values from 1 to 100
  Matrix<BigDecimal> matrix = new Matrix<>(
      IntStream.range(1, 101)
               .mapToObj(i -> IntStream.range(1, 101)
                                       .mapToObj(BigDecimal::valueOf)
                                       .collect(Collectors.toList()))
               .collect(Collectors.toList())
  );
  ```

- **Key Methods**:
    - `getRows()`: Returns the number of rows.
    - `getColumns()`: Returns the number of columns.
    - `appendMatrix(Matrix<T>)`: Appends rows of another matrix if compatible.

---

### 2. **`MatrixCalculator`**
A utility class (or interface) providing common matrix operations on a single matrix such as transposition.

---

### 3. **`MatricesCalculator`**
Focused on more complex operations involving multiple matrices, including dot product calculations.

A concrete implementation is required for calculation the dot product.

#### **Dot Product Example**:
   ```java
   Matrix<BigDecimal> matrix1 = ... // Initialize matrix1
   Matrix<BigDecimal> matrix2 = ... // Initialize matrix2
     
   // Perform a dot product operation
   Matrix<BigDecimal> resultMatrix = BigDecimalMatricesCalculator.dot(matrix1, matrix2);
   ```

#### **Parallel Dot Product**:
For enhanced performance, `parallelDot` enables multithreaded computation:
   ```java
    Matrix<BigDecimal> resultMatrix = BigDecimalMatricesCalculator.parallelDot(matrix1, matrix2);
   ```

- **Multithreading Benefits**:
    - Splits the computation across threads to utilize multicore CPUs.
    - Offers significant performance improvements for large matrices.

---


## How to Use This Project

1. **Set Up the Project**:
    - Clone the repository and run gradle build to create a JAR


---

## License

This project is licensed under the [MIT License](LICENSE). Feel free to use, modify, and distribute it as per the license terms.

---

## Contributors

- **Ramon Singh**
- Contributions are welcome! Open an issue or submit a pull request.

---
