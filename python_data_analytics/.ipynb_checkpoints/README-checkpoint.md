# PYTHON DATA ANALYTICS

## Introduction

London Gift Shop (LGS) is a UK-based online store that sells giftware. Many of its customers are wholesalers. The company has been running online shops for more than 10 years, but revenue has not grown in recent years.

LGS requested a Data Engineer from Jarvis Consulting to analyze their dataset, as the company lacks sufficient internal resources to take on additional projects. As the assigned Data Engineer, I was tasked with creating a proof of concept (PoC) to help the LGS marketing team better understand customer shopping behaviour. The marketing team will use these analytics to develop targeted marketing campaigns — including email, events, and targeted promotions — to attract both new and existing customers.

This PoC delivers a Jupyter Notebook with insights aimed at answering why LGS has not grown in revenue in recent years. The analysis attempts to answer the following questions:

1. What items are selling the most?

2. Who are the top customers and what do they have in common?

3. How can we group customers into meaningful segments?

4. What items are being returned?

5. What are the high seasons for revenue?

<!-- TODO: Describe the business context of this project in your own words.
     What does LGS do? What problem are they trying to solve with data analytics?
     What value does this project deliver to them? -->

<!-- TODO: Describe how LGS would use your analytic results.
     What decisions or actions do your findings enable? -->

### Technologies

This PoC was implemented using thje following technologies and tools:

- **Python** — primary programming language for all data processing and analytics
- **Jupyter Notebook** — interactive development environment used to write, run, and present analytics
- **Pandas** — core library for data loading, cleaning, transformation, and aggregation
- **NumPy** — used for numerical operations and array-based calculations
- **Matplotlib** — used for data visualization including histograms, line charts, bar charts, and box plots
- **psycopg2** — used to connect to and query the PostgreSQL data warehouse from Python
- **PostgreSQL (PSQL)** — data warehouse provisioned via Docker to store and query the retail transaction data
- **Docker** — used to provision and manage both the PostgreSQL and Jupyter Notebook containers in isolated environments

Finally the project is delievered as a Jupyter Notebook and executive presentation.

---

# Implementation

### Project Architecture

The project architecture was built on transaction data spanning 01/12/2009 to 09/12/2011, retrieved from a SQL file. That data was loaded into a PostgreSQL instance running inside a Docker container. A second Docker container ran a Jupyter Notebook image, and a Docker network was used to connect the two containers. From within the Jupyter Notebook, psycopg2 was used to pull the data from PostgreSQL database, which was then analyzed using Python, Pandas, and NumPy.

<!-- TODO: Insert your architecture diagram below. Replace the path with the actual location of your image. -->

![Architecture Diagram](./final.drawio.png)

# Data Analytics and Wrangling

> Notebook can be found here : [Jupyter Notebook](./retail_data_analytics_wrangling.ipynb)

<!-- TODO: Discuss how you would use the data to help LGS increase revenue.
     What patterns or insights did you find? What marketing or business strategy would you recommend based on your analysis? -->

### Takeaways:

The analysis was structured around five business questions, each designed to give the LGS marketing team a concrete basis for action:

**Top-selling items** — by identifying which products generate the most volume and revenue, LGS can prioritize inventory and feature these products prominently in campaigns and promotions.
**Top customers** — understanding who the highest-value customers are and what they have in common allows LGS to build a retention strategy targeting this group with loyalty incentives or exclusive offers.
**Customer segmentation** — grouping customers by purchasing behaviour (e.g. RFM analysis) enables the marketing team to tailor messaging by segment rather than applying a one-size-fits-all approach.
**Returned items** — identifying which products are returned most frequently surfaces quality or expectation-mismatch issues that, if resolved, could directly reduce revenue loss.
**Revenue seasonality**— pinpointing high-revenue periods allows LGS to time campaigns, stock levels, and promotional spend to align with natural buying peaks rather than working against them.

---

# Improvements

Given more time, the following enhancements would be prioritized:

1. **Interactive Dashboard** — replace static Matplotlib visualizations with an interactive dashboard using a tool such as Plotly Dash or Streamlit, allowing stakeholders to filter and explore the data by date range, product, or region without modifying code.

2. **Automated Data Pipeline** — replace the manual data loading process with a scheduled ETL pipeline so the data warehouse stays current automatically, removing the need for manual SQL file imports.

3. **Predictive Modelling** — extend the analysis from descriptive to predictive by building a churn-risk or demand forecasting model, enabling LGS to act on future trends rather than only interpret historical patterns.
