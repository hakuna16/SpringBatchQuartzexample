# SpringBatchQuartzexample



## What is Batch Job?
-	A batch job consists of one or more steps. Each step is responsible of completing one logical task. 
-	Every step reads input data, processes the input data, and writes the processed data to the configured output. 
-	If the batch job has more one step, the output of a step is often used as an input of next.
## What is spring batch?
Spring batch is a lightweight framework which is used to develop Batch Applications that are used in Enterprise Applications.
In addition to bulk processing, this framework provides functions for –
-	Including logging and tracing
-	Transaction management
-	Job processing statistics
-	Job restart
-	Skip and Resource management

## Batch Processing Strategies
1.	Conversion Applications: 
-	For each type of file supplied by or generated to an external system, a conversion application will need to be created to convert the transaction records supplied into a standard format required for processing. 
This type of batch application can partly or entirely consist of translation utility modules.
2.	Validation Applications: 
-	Validation applications ensure that all input/output records are correct and consistent. 
Validation is typically based on file headers and trailers, checksums and validation algorithms as well as record level cross-checks.
3.	Extract Applications: 
-	An application that reads a set of records from a database or input file, selects records based on predefined rules, and writes the records to an output file.
4.	Extract/Update Applications: 
-	An application that reads records from a database or an input file, and makes changes to a database or an output file driven by the data found in each input record.
Processing and Updating Applications: 
An application that performs processing on input transactions from an extract or a validation application. 
The processing will usually involve reading a database to obtain data required for processing, potentially updating the database and creating records for output processing.
Output/Format Applications: 
-	Applications reading an input file, restructures data from this record according to a standard format, and produces an output file for printing or transmission to another program or system.
-	Each application may use one or more of standard utility steps

Sort - A Program that reads an input file and produces an output file where records have been re-sequenced according to a sort key field in the records. 
Sorts are usually performed by standard system utilities.
Split - A program that reads a single input file, and writes each record to one of several output files based on a field value. 
Splits can be tailored or performed by parameter-driven standard system utilities.

Merge - A program that reads records from multiple input files and produces one output file with combined data from the input files. 
Merges can be tailored or performed by parameter-driven standard system utilities.

Batch applications can additionally be categorized by their input source:
-	Database-driven applications are driven by rows or values retrieved from the database.
-	File-driven applications are driven by records or values retrieved from a file.
-	Message-driven applications are driven by messages retrieved from a message queue
Typical processing options for batch are:
-	Normal processing in a batch window during off-line
-	Concurrent batch / on-line processing
-	Parallel processing of many different batch runs or jobs at the same time
-	Partitioning (i.e. processing of many instances of the same job at the same time)
-	A combination of these
1.	Normal processing :
- in a batch window for simple batch processes running in a separate batch window, where the data being updated is not required by on-line users or other batch processes, concurrency is not an issue and a single commit can be done at the end of the batch run.
      2. Concurrent batch / on-line processing
- Batch applications processing data that can simultaneously be updated by on-line users, should not lock any data (either in the database or in files) which could be required by on-line users for more than a few seconds
Option to minimize physical locking is to have a logical row-level locking implemented using either an Optimistic Locking Pattern or a Pessimistic Locking Pattern.
1.	Optimistic locking 
-	It assumes a low likelihood of record contention. 
-	It typically means inserting a timestamp column in each database table used concurrently by both batch and on-line processing. 
-	When an application fetches a row for processing, it also fetches the timestamp. 
-	As the application then tries to update the processed row, the update uses the original timestamp in the WHERE clause. 
-	If the timestamp matches, the data and the timestamp will be updated successfully. 
-	If the timestamp does not match, this indicates that another application has updated the same row between the fetch and the update attempt and therefore the update cannot be performed.

2.	Pessimistic locking 
-	It is any locking strategy that assumes there is a high likelihood of record contention and therefore either a physical or logical lock needs to be obtained at retrieval time. 
-	One type of pessimistic logical locking uses a dedicated lock-column in the database table. 
-	When an application retrieves the row for update, it sets a flag in the lock column. 
-	With the flag in place, other applications attempting to retrieve the same row will logically fail. 
-	When the application that set the flag updates the row, it also clears the flag, enabling the row to be retrieved by other applications.
-	 Please note, that the integrity of data must be maintained also between the initial fetch and the setting of the flag, for example by using db locks (e.g., SELECT FOR UPDATE).

3.	Parallel Processing :
Parallel processing allows multiple batch runs / jobs to run in parallel to minimize the total elapsed batch processing time
key issues in parallel processing include load balancing and the availability of general system resources such as files, database buffer pools etc. Also note that the control table itself can easily become a critical resource.

4.	Partitioning 
-	Using partitioning allows multiple versions of large batch applications to run concurrently. 
-	The purpose of this is to reduce the elapsed time required to process long batch jobs.
-	Processes which can be successfully partitioned are those where the input file can be split and/or the main database tables partitioned to allow the application to run against different sets of data.
-	processes which are partitioned must be designed to only process their assigned data set. 
-	A partitioning architecture has to be closely tied to the database design and the database partitioning strategy. 
Please note, that the database partitioning doesn't necessarily mean physical partitioning of the database, although in most cases this is advisable


5.	Partitioning Approaches

1.	Fixed and Even Break-Up of Record Set 
o	This involves breaking the input record set into an even number of portions (e.g. 10, where each portion will have exactly 1/10th of the entire record set). 
o	Each portion is then processed by one instance of the batch/extract application
o	In order to use this approach, preprocessing will be required to split the recordset up. 
o	The result of this split will be a lower and upper bound placement number which can be used as input to the batch/extract application in order to restrict its processing to its portion alone

2.	Breakup by a Key Column
-	This involves breaking up the input record set by a key column such as a location code, and assigning data from each key to a batch instance. 
-	In order to achieve this, column values can either be
3. Assigned to a batch instance via a partitioning table
4. Assigned to a batch instance by a portion of the value
5. Breakup by Views
-	This approach is basically breakup by a key column, but on the database level. 
-	It involves breaking up the recordset into views. 
-	These views will be used by each instance of the batch application during its processing. 
-	The breakup will be done by grouping the data.
-	With this option, each instance of a batch application will have to be configured to hit a particular view (instead of the master table). 
-	Also, with the addition of new data values, this new group of data will have to be included into a view
6.	Addition of a Processing Indicator
-	This involves the addition of a new column to the input table, which acts as an indicator.
-	 As a preprocessing step, all indicators would be marked to non-processed. 
-	During the record fetch stage of the batch application, records are read on the condition that that record is marked non-processed, and once they are read (with lock), they are marked processing. 
-	When that record is completed, the indicator is updated to either complete or error. 
-	Many instances of a batch application can be started without a change, as the additional column ensures that a record is only processed once.
With this option, I/O on the table increases dynamically. In the case of an updating batch application, this impact is reduced, as a write will have to occur anyway
7. Extract Table to a Flat File
- This involves the extraction of the table into a file. This file can then be split into multiple segments and used as input to the batch instances.
8. Use of a Hashing Column
-	This scheme involves the addition of a hash column (key/index) to the database tables used to retrieve the driver record. 
-	This hash column will have an indicator to determine which instance of the batch application will process this particular row. 
-	For example, if there are three batch instances to be started, then an indicator of 'A' will mark that row for processing by instance 1, an indicator of 'B' will mark that row for processing by instance 2, etc.
The procedure used to retrieve the records would then have an additional WHERE clause to select all rows marked by a particular indicator. The inserts in this table would involve the addition of the marker field, which would be defaulted to one of the instances (e.g. 'A').
What's New in Spring Batch 3.0? 
The Spring Batch 3.0 release has five major themes:
-	JSR-352 Support
-	Upgrade to Support Spring 4 and Java 8
-	Promote Spring Batch Integration to Spring Batch
-	JobScope Support
-	SQLite Support

JSR-352 Support
-	JSR-352 is the new java specification for batch processing. 
-	Heavily inspired by Spring Batch, this specification provides similar functionality to what Spring Batch already supports.
<? xml version="1.0" encoding="UTF-8"?>
<job id="myJob3" xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="1.0">
    <step id="step1" >
        <batchlet ref="testBatchlet" />
    </step>
</job>



The Anatomy of a Spring Batch Job
A Spring Batch job consists of the following components:
-	The Job represents the Spring Batch job. Each job can have one or more steps.
-	The ItemReader reads the input data and provides the found items one by one. 
o	An ItemReader belongs to one step and each step must have only one ItemReader.
-	The ItemProcessor transforms items into a form that is understood by the ItemWriter one item at a time. 
o	An ItemProcessor belongs to one step and each step can have one ItemProcessor.
o	The ItemWriter writes an information of an item to the output one item at a time. 
o	An ItemWriter belongs to one step and a step must have only one ItemWriter
Spring batch follows the traditional batch architecture where a job repository does the work of scheduling and interacting with the job.
A job can have more than one steps – and every step typically follows the sequence of reading data, processing it and writing it.

Application − This component contains all the jobs and the code we write using the Spring Batch framework.
Batch Core − This component contains all the API classes that are needed to control and launch a Batch Job.
Batch Infrastructure − This component contains the readers, writers, and services used by both application and Batch core components.

Modules of Spring Batch
Spring Batch provides the following modules:
-	The spring-batch-infrastructure module contains the common readers and writers, and provides services for application developers and the core module.
-	The spring-batch-core module contains the classes that are required to launch and control Spring Batch jobs.
-	The spring-batch-test module provides support for writing automated tests to Spring Batch jobs.
-	The spring-batch-integration module helps us to integrate Spring Batch with Spring Integration.
Job:
-	A Job is an entity that encapsulates an entire batch process
-	A job is the batch process that is to be executed. It runs from start to finish without interruption.
We will configure a job in Spring Batch using an XML file or a Java class. Following is the XML configuration of a Job in Spring Batch.
<job id = "jobid"> 
   <step id = "step1" next = "step2"/> 
   <step id = "step2" next = "step3"/> 
   <step id = "step3"/> 
</job>
A Batch job is configured within the tags <job></job>. 
It has an attribute named id. Within these tags, we define the definition and ordering of the steps.

JobParameters
-	JobParameters is a set of parameters used to start a batch job. 
-	They can be used for identification or even as reference data during the run:


Job Execution
-	A JobExecution refers to the technical concept of a single attempt to run a Job.
-	 An execution may end in failure or success, but the JobInstance corresponding to a given execution will not be considered complete unless the execution completes successfully.
JobExecution Properties
-	status	
o	A BatchStatus object that indicates the status of the execution. 
o	While running, it's BatchStatus.STARTED, if it fails, it's BatchStatus.FAILED, and if it finishes successfully, it's BatchStatus.COMPLETED
-	startTime	
-	endTime
-	exitStatus
-	createTime
-	lastUpdated
-	executionContext
-	failureExceptions
o	exception is encountered during the failure of a Job.	
Restartable − In general, when a job is running and we try to start it again that is considered as restart and it will be started again. To avoid this, you need to set the restartable value to false as shown below.
<job id = "jobid" restartable = "false" >
</job>
### Step
-	A step is an independent part of a job which contains the necessary information to define and execute the job (its part).

### Readers, Writers, and Processors
-	An item reader reads data into a Spring Batch application from a particular source, whereas an item writer writes data from the Spring Batch application to a particular destination.
An Item processor is a class which contains the processing code which processes the data read into the spring batch. If the application reads "n" records, then the code in the processor will be executed on each record.
When no reader and writer are given, a tasklet acts as a processor for Spring Batch. It processes only a single task. For example, if we are writing a job with a simple step in it where we read - data from MySQL database and process it and write it to a file (flat), then our step uses –
-	A reader which reads from MySQL database.
-	A writer which writes to a flat file.
-	A custom processor which processes the data as per our wish.
<job id = "helloWorldJob"> 
   <step id = "step1"> 
      <tasklet> 
         <chunk reader = "mysqlReader" writer = "fileWriter" 
            processor = "CustomitemProcessor" ></chunk> 
      </tasklet> 
   </step> 
</ job>
### JobRepository
A Job repository in Spring Batch provides Create, Retrieve, Update, and Delete (CRUD) operations for the JobLauncher, Job, and Step implementations.
<job-repository id = "jobRepository"/> 
-	The options and their default values.
<job-repository id = "jobRepository" 
   data-source = "dataSource" 
   transaction-manager = "transactionManager" 
   isolation-level-for-create = "SERIALIZABLE" 
   table-prefix = "BATCH_" 
   max-varchar-length = "1000"/>
### JobLauncher
JobLauncher is an interface which launces the Spring Batch job with the given set of parameters. SampleJoblauncher is the class which implements the JobLauncher interface. Following is the configuration of the JobLauncher.
<bean id = "jobLauncher" 
   class = "org.springframework.batch.core.launch.support.SimpleJobLauncher"> 
   <property name = "jobRepository" ref = "jobRepository" /> 
</bean>
### JobInstance
A JobInstance represents the logical run of a job; it is created when we run a job. Each job instance is differentiated by the name of the job and the parameters passed to it while running.
If a JobInstance execution fails, the same JobInstance can be executed again. Hence, each JobInstance can have multiple job executions.
Spring Batch – Application
A Spring Batch application has the following types of files
-	Configuration file (XML file)
-	Tasklet/processor (Java class)
-	Java class with setters and getters (Java class (bean))
-	Mapper class (Java class)
-	Launcher class (Java class)

### Configuration File
The configuration file (XML) contains the following –
-	The job and step definitions.
-	Beans defining readers and writers.
Definition of components like JobLauncher, JobRepository, Transaction Manager, and Data Source.
-	For better understanding, we have divided this in to two files the 
o	job.xml file (defines job, step, reader and writer) and 
o	context.xml file (job launcher, job repository, transaction manager and data source).
Mapper Class
The Mapper class, depending upon the reader, implements interfaces such as row mapper, field set mapper, etc. It contains the code to get the data from the reader and to set it to a Java class with setter and getter methods (Java Bean).
Tasklet/processor
The Tasklet/processor class contains the processing code of the Spring Batch application. A processor is a class which accepts an object that contains the data read, processes it, and returns the processed data (in the form object).
Launcher class
This class (App.java) contains the code to launch the Spring Batch application.











Spring Batch - Configuration
1.	we will configure the job, step, JobLauncher, JobRepository, Transaction Manager, readers, and writers using the XML tags provided in the Spring Batch namespace

Job
This tag is used to define/configure the job of the Spring Batch.
-	Id
It is the Id of the job, it is mandatory to specify value to this attribute.
-	restartable
This is the attribute which is used to specify whether the job is restartable or not. This attribute is optional.
Step
This tag is used to define/configure the steps of a Spring Batch job.
-	Id
It is the Id of the job, it is mandatory to specify value to this attribute.
-	next
It is the shortcut to specify the next step.
-	parent
It is used to specify the name of the parent bean from which the configuration should inherit.
Following is the XML configuration of the step of a Spring Batch.
<job id = "jobid"> 
   <step id = "step1" next = "step2"/> 
   <step id = "step2" next = "step3"/> 
   <step id = "step3"/> 
</job>
Chunk
This tag is used to define/configure a chunk of a tasklet
-	reader
o	It represents the name of the item reader bean. 
o	It accepts the value of the type org.springframework.batch.item.ItemReader.
-	Writer
o	It represents the name of the item reader bean. 
o	It accepts the value of the type org.springframework.batch.item.ItemWriter.
-	Processor
o	It represents the name of the item reader bean. 
o	It accepts the value of the type org.springframework.batch.item.ItemProcessor.	
-	commit-interval
o	It is used to specify the number of items to be processed before committing the transaction.
JobRepository
-	The JobRepository Bean is used to configure the JobRepository using a relational database. 
-	This bean is associated with the class of type org.springframework.batch.core.repository.JobRepository.

1.	dataSource
-	It is used to specify the bean name which defines the dataSource.
2.	transactionManager
-	It is used specify the name of the bean which defines the transactionManager.
3.	databaseType
-	It specifies the type of the relational database used in the job repository.
<bean id = "jobRepository" 
   class = "org.springframework.batch.core.repository.support.JobRepositoryFactoryBean"> 
   <property name = "dataSource" ref = "dataSource" /> 
   <property name = "transactionManager" ref="transactionManager" /> 
   <property name = "databaseType" value = "mysql" /> 
</bean>
JobLauncher
-	The JobLauncher bean is used to configure the JobLauncher. 
-	It is associated with the class org.springframework.batch.core.launch.support.SimpleJobLauncher (in our programs). 
-	This bean has one property named jobRepository, and it is used to specify the name of the bean which defines the jobRepository.
<bean id = "jobLauncher" 
   class = "org.springframework.batch.core.launch.support.SimpleJobLauncher"> 
   <property name = "jobRepository" ref = "jobRepository" /> 
</bean>

TransactionManager
-	The TransactionManager bean is used to configure the TransactionManager using a relational database. 
-	This bean is associated with the class of type org.springframework.transaction.platform.TransactionManager.
<bean id = "transactionManager"
   class = "org.springframework.batch.support.transaction.ResourcelessTransactionManager" />
Chunks:
A chunk is a child element of the tasklet. It is used to perform read, write, and processing operations. We can configure reader, writer, and processors using this element, within a step as 
<batch: job id = "helloWorldJob"> 
   <batch: step id = "step1"> 
      <batch: tasklet> 
         <batch: chunk reader = "cvsFileItemReader" writer = "xmlItemWriter" 
            processor = "itemProcessor" commit-interval = "10"> 
         </batch: chunk> 
      </batch: tasklet> 
   </batch: step> 
</batch: job>
ItemReader
It is the entity of a step (of a batch process) which reads data. An ItemReader reads one item a time. Spring Batch provides an Interface ItemReader.
The predefined ItemReader classes provided by Spring Batch to read from various sources.
Reader	Purpose
FlatFIleItemReader	To read data from flat files.
StaxEventItemReader		To read data from XML files.
StoredProcedureItemReader	To read data from the stored procedures of a database.
JDBCPagingItemReader	To read data from relational databases database
MongoItemReader	To read data from MongoDB.
Neo4jItemReader	To read data from Neo4jItemReader.
ItemWriter
-	It is the element of the step of a batch process which writes data.
-	 An ItemWriter writes one item a time. Spring Batch provides an Interface ItemWriter
Item Processor
-	An ItemProcessor is used to process the data. 
-	When the given item is not valid it returns null, else it processes the given item and returns the processed result. 
-	The interface ItemProcessor<I,O> represents the processor.
Tasklet class
-	When no reader and writer are given, a Tasklet acts as a processor for Spring Batch. 
-	It processes only single task.
We can define a custom item processor by implementing the interface ItemProcessor of the package org.springframework.batch.item.ItemProcessor.
Our Spring Batch application contains the following files −
Configuration file – 
-	This is an XML file where we define the Job and the steps of the job. 
-	(If the application involves readers and writers too, then the configuration of readers and writers is also included in this file.)
Context.xml – 
-	In this file, we will define the beans like job repository, job launcher and transaction manager.
Tasklet class – 
-	In this class, we will write the processing code job (In this case, it displays a simple message)
Launcher class – 
-	In this class, we will launch the Batch Application by running the Job launcher
jobConfig.xml
<beans xmlns = "http://www.springframework.org/schema/beans" 
   xmlns: batch = "http://www.springframework.org/schema/batch" 
   xmlns:xsi = "http://www.w3.org/2001/XMLSchema-instance" 
   xsi:schemaLocation = "http://www.springframework.org/schema/batch 
      http://www.springframework.org/schema/batch/spring-batch-2.2.xsd
      http://www.springframework.org/schema/beans
      http://www.springframework.org/schema/beans/spring-beans-3.2.xsd "> 
   <import resource="context.xml" />      
   <!-- Defining a bean --> 
   <bean id = "tasklet" class = "a_sample.MyTasklet" />  
   <!-- Defining a job--> 
   <batch:job id = "helloWorldJob">  
      <!-- Defining a Step --> 
      <batch:step id = "step1"> 
         <tasklet ref = "tasklet"/>   
      </batch:step>    
   </batch:job>  
</beans>

Context.xml
<beans xmlns = "http://www.springframework.org/schema/beans" 
   xmlns:xsi = http://www.w3.org/2001/XMLSchema-instance" 
   xsi:schemaLocation = "http://www.springframework.org/schema/beans 
      http://www.springframework.org/schema/beans/spring-beans-3.2.xsd">  
    <bean id = "jobRepository"   
  class="org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean"> 
      <property name = "transactionManager" ref = "transactionManager" /> 
   </bean>     
      <bean id = "transactionManager" 
     class = "org.springframework.batch.support.transaction.ResourcelessTransactionManager" />  
   <bean id = "jobLauncher" 
      class = "org.springframework.batch.core.launch.support.SimpleJobLauncher"> 
      <property name = "jobRepository" ref = "jobRepository" /> 
   </bean> 
</beans> 	


Tasklet.java
import org.springframework.batch.core.StepContribution; 
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;  
public class MyTasklet implements Tasklet {
   @Override 
   public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {  
      System.out.println("Hello This is a sample example of spring batch"); 
      return RepeatStatus.FINISHED; 
   } 
} 
App.java
import org.springframework.batch.core.Job; 
import org.springframework.batch.core.JobExecution; 
import org.springframework.batch.core.JobParameters; 
import org.springframework.batch.core.launch.JobLauncher; 
import org.springframework.context.ApplicationContext; 
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App { 
   public static void main(String[] args)throws Exception { 
  
      // System.out.println("hello"); 
      String[] springConfig  =  {"a_sample/job_hello_world.xml"};  
      
      // Creating the application context object  
      ApplicationContext context = new ClassPathXmlApplicationContext(springConfig); 
      
      // Creating the job launcher 
      JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher"); 
  
      // Creating the job 
      Job job = (Job) context.getBean("helloWorldJob"); 
  
      // Executing the JOB 
      JobExecution execution = jobLauncher.run(job, new JobParameters()); 
      System.out.println("Exit Status : " + execution.getStatus()); 
   }    
}
