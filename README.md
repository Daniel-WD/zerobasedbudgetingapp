# zerobasedbudgetingapp

## Description

This is a test application to develop a basic zero based budgeting app.
The user is able to add and remove several transactions of a single bankaccount.
The money he receives can be budgeted to different categories. Every transaction
has a specific category. The user can add/remove categories any time. Each category
shows the money that is left. Also there are warnings for overspending. A transaction
causes a change in the budgeting system. A positive transaction will increase the amount
of money that can be budgeted. A negative transaction will decrease the amount of the
category it is categorized in. The "to be budgeted" value is a category itself.
Budgeting and transactions are each divided by months.

Things to consider after MVP:
- Creditcard
- Scheduled/Repeated transactions
- Grafical analysis of spent money in each category
- Multiple bankaccounts
- Top level categories for categories
- Hidden categories
- Transaction rememberence to the same payee
- Transaction tags/colors
- Support split categories per transaction
- Set if transaction is registered in bank account
- New Transaction guidance

## Features

- Hold one bank account
- Add/remove transactions
- Transactions have categories
- Money can be budgeted to different categories
- Add/remove categories
- Categories show money thats left
- Categories show warning when overspent
- Positive transaction increase money in category/"to be budgeted" amount
- Negative transaction decrease money in category/"to be budgeted" amount
- Time division by months

## Screens

### Categories
- List of categories
- Category contains currently budgeted and available money
- Click category to change budgeted value
- Shows "to be budgeted" value
- Split by months

Categories

![alt text](/prototypes/pngs/categories.png)

Budget

![alt text](/prototypes/pngs/categories%20-%20Change%20Budget%20of%20category.png)

Select month

![alt text](/prototypes/pngs/categories%20–%20select%20month.png)


### Create/Create Transaction
- Value of transaction
- Positive or negative transaction
- Set category, payee, description, date
- Delete

Create transaction

![alt text](/prototypes/pngs/Create%20transaction.png)

Edit transaction

![alt text](/prototypes/pngs/Edit%20transaction.png)

Set Payee

![alt text](/prototypes/pngs/Create-Edit%20transaction%20-%20Set%20Payee.png)

Set Category

![alt text](/prototypes/pngs/Create-Edit%20transaction%20-%20Set%20Category.png)

New Payee

![alt text](/prototypes/pngs/Create-Edit%20transaction%20-%20New%20Payee.png)


### All transactions
- List of transactions
- Entry contains: payee, value, date
- Entries ordered by date
- Split by months

Transactions

![alt text](/prototypes/pngs/Transactions.png)


### Organize Categories
- Add category
- Remove category
- Set category order
- Edit

Organize Categories

![alt text](/prototypes/pngs/Organize%20Categories.png)

## Diagrams

### Class Diagram

![alt text](/architecture/class_diagram.jpg)
