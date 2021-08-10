# REMARKS

## Some remarks related to APIv2 and payload in step definitions

<span style="color:yellow">1. There are several UPDATE request that do not do their job,
such as User does not update the fields such as description and code.
Can we create, for example, a mechanism to compare the result of update request
with the result before being updated ? MAY ASK WASSIM BEFORE DOING THIS JOB SINCE IT COULD BE
ALREADY DONE WITH POSTMAN.
From this, we can create a report that tells which
methods do not do their job. </span>



<span style="color:yellow"> 2. Some other entities, such as TradingCurrency uses "non-normalized" fields such as
'prDescription' (instead of 'description'). How to deal with this problem???
A possible solution is to create a mapping between "non-normalized" fields
and "normalized" fields. We can create a dictionary as follows:
</span>

<span style="color:yellow">
   description = { TradingCurrency: "prDescription", User: "description"  }
</span>

<span style="color:yellow">This problem has been resolved by introducing class Dictionaries_API.java.
However, we may need to create an automatic mechanism to feed intelligently 
these dictionaries.
</span>

<span style="color:yellow">
With this dictionary, while dealing with the update request for User, the field
"description" will be used; and the field "prDescription" will be used while
dealing with the update request for TradingCurrency
</span>


3. Can we make feature file become more generic ?
For this problem, we could process as follows: 
   - Initially, we propose to choose get entity base on the field id, this field
     is sufficient to get the entity.
   - Next, other remaining fields such as code, description, etc. will be randomly 
     generated. The aim of this step is to allow users not to write these remaining
     fields. However, these fields must be always modified in .feature files.
   - For example, to update an entity of Seller, we can always update some fields
   such as description, code, tradingCurrency, tradingCountry, etc. These fields will 
   be rewritten with the values provided by users.
   


## Some remarks related to generation process

1. We need to determine which variables are required in the step definition

2. In this example below, the generation process takes into account the number of 
parameters in the phrase "Fields filled by ..." to generate an appropriate Java method

![Tux, Scenario of entity update](assets/images/Update_entity.jpg)
