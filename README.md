<h1>Description</h1>
a CLI application that processes simplified trade data from a CSV file to calculate and display intraday cash position aggregations (PnL) in USD
Written in Java 17 using Maven

<h3>Instructions</h3>
Pull the project to your local environment
Run via the main class in your terminal/commandline or via your IDE

<h2>To Do/Improvements</h2>

- [ ] Improve Test Coverage
  - [ ] Add unit tests for the Main Class
  - [ ] Improve tests covering the Trade Aggregator 
  - [ ] Add Integration/EndtoEnd tests for user inputs using Dependency Injection or Create Wrapper classes
  - [ ] create a ExitProgramException class that extends RuntimeException to handle errors and system exit better
- [ ] Implement some logging to log exceptions for debugging
- [ ] create an Aggregations Concurrent HashMap that can store each aggregation
- [ ] implement a database storage solution to store the aggregations and pass them to the UI or other applications
- [ ] create an API for consumption by other applications
- [ ] Add a GUI using a JavaScript framework
  - [ ] getOriginalTrades() - method to return the original list of trades (pre conversion) to be used by the UI to persist trade information
  - [ ] displayTradesFromMap() - method to display trades from the trades map
  - [ ] getTotalTradesAdded() - method to Display the total number of trades added
  - [ ] getCanceledTradesCount() - method to debug and to add functionality to the UI
  - [ ] getAmendedTradesCount() - method to debug and to add functionality to the UI
  - [ ] getAggregatedTrades() - method to return all the trades in an aggregation add functionality to the UI
  - [ ] displayAggregation() - method to display the Number of trades for a given aggregationKey and add functionality to the UI
