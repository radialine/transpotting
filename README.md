This is the documentation for Lab1 of course TDA384.

I) Placement of the sensors  

   We decided to think in terms of "states" to solve the problem. So we created 11 independent zones that a train could go through. 
   Entering or leaving a zone triggers a set of commands that is specific to the zone. In order to do so, we needed to mark out each zones with sensors. 

   Here is a state diagram with the placement of the sensors:


end of track <=>sensor 12,11 <=> (State 3) <=> sensor 5,11 <=>  		
									    (State 5) ... 							
end of track <=>sensor 12,13 <=> (State 4) <=> sensor 3,13 <=>   		


		<=> sensor 7,9 <=> (State 6) <=> sensor 12,9 <=>
... (State 5)							(State 8)...
		<=> sensor 6,10 <=> (State 7) <=> sensor 13,10 <=>


		<=> sensor 14,7 <=> (State 10) <=> sensor 10,7 <=>
... (State 8)							(State 11)...
		<=> sensor 15,8 <=> (State 9) <=> sensor 10,8 <=>


		<=> sensor 6,7 <=> (State 12) <=> sensor 12,3 <=>end of track
... (State18)							
		<=> sensor 8,5 <=> (State 13) <=> sensor 13,5 <=>end of track


II) Choice of critical sections 

We set three critical sections which are controlled by semaphore5c, semaphore8c and semaphore11c. 
	The first two represent single west and east tracks, only the train acquires the semaphore can pass it. 
	The third represents the crossroad, only the train acquires the semaphore can pass it.  
We also use semaphore 34, semaphore 67 and semaphore 910 to help the train to choose one track in paralleled tracks. 
	If one track holds semaphore34, it goes to state 4 by default. 
	If one track holds semaphore67, it goes to state 7 by default. 
	If one track holds semaphore910, it goes to state 9 by default. 

Each train tries to acquire semaphore of the demanding critical section or parallel tracks before entering the section. 
Each train releases semaphore after passing through the critical section or paralled tracks. 


III) Maximum train speed and the reason for it 

  The maximum train speed with our solution is 18. It is above the required 15. 


IV) How you tested your solution
  
  We tested our solution with different speed combinations that seemed relevant including:
	- symmetric speeds like 15 15
	- different kinds of asymmetric speeds like 7 15, 15 3 ...
