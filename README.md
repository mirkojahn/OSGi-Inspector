# The OSGi-Inspector project

The inspector provides a simple, yet powerful tool to get a hold on errors and
problems you might be facing while working within an OSGi container. It integrates
seamlessly into your existing projects and provides you with a rich set of
information about your runtime otherwise hidden in the system.

## Features since 0.1.0:

- discovery of unsatisfied service requests
- tracking of pending service requests
- capabilities of the OSGi runtime (just the core for now)
- assembly for a simple klick and got test drive (based on Pax_Runner)
- tutorial to show the very core idea (no ui there yet)
- UI to visualize parts of the tracked data (http://localhost:8080/inspector)
- reasoner API to allow for plug-able reasoning about runtime problems
- simple reasoner for ClassNotFoundException
- Framework Error Event reasoner (partially implemented)

## Upcoming features/ work in progress:
- application level life cycle management to better know in which state your application actually is.
-- provides an extendable API reusable by 3rd parties
-- implements some contributors to track f.i. the "Spring DM" start-up process
- enhanced error analysis of runtime exceptions
-- analysis of OSGi exceptions with descriptions aimed to helpful solving the problem
-- ClassNotFoundException Reasoner to identify the cause of the exception
-- API to provide your own Reasoners in order to solve domain specific, but deterministic problems
-- API to directly interact with the reasoning engine
- ground works for a native agent capable of analyzing the memory consumption of your various bundles
-- very basic tests to get the set-up running
-- API to query the agent from within the OSGi runtime at test
- Eclipse update-site integration for better installation support

## General Goals:
- offer convenient methods to analyze internals of the OSGi runtime like:
	- unsatisfied and pending service requests
	- mismatching version constraints on packages
	- memory consumption on bundle level
	- capabilities of the OSGi runtime (which core services in what version)
- statistics about used frameworks and packages
- runtime bundle wiring (on package level)
- lifecycle information (when did the framework really start)
- http service to provide a convenient UI
- console extension to use the command line (unfortunately this is implementation dependent)
- plug-able reasoner to help analyze errors/problems

# Internal Requirements the inspector agreed to comply to (at least the very core):
- OSGi R4 runtime (best results with R4.2)
- no dependencies on 3rd party libraries or special OSGi framework implementations

# Requirements for your application in order to successfully use the inspector:
- the core bundle has to be started before any other bundle (required in order to track service requests)
