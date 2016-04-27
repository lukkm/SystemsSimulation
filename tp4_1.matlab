##Systems Simulation

#Parameters


STEPS = 200;
dt = 0.1;

m = 70;
k = power(10,4);
gamma = 100;
tf = 5;

#initial conditions
r0 = 1;
v0 = - gamma / (m/2);


bandwidth = gamma/(2*m);
r = ones(STEPS,2); #format = position , time

for i = 1:STEPS
	t = i * dt;
	r(i,1) = exp( - bandwidth * t) * cos( sqrt(k/m - power(bandwidth,2)) * t);
	r(i,2) = t;
end
#r # to print

save dampingExact.txt r

##Euler

euler = ones(STEPS, 3);#format position, time, velocity
euler(1,1) = r0;
euler(1,2) = dt;
euler(1,3) = v0;
for i = 2:STEPS
	euler(i,1) = euler(i-1,1) + dt *euler(i-1,2)+ (power(dt,2)/2*m);
	f = -k * euler(i-1,1) - gamma * euler(i-1,2);
	euler(i,2) = euler(i-1,2) + (dt/m) * f;
end
euler # to print

save dampingEuler.txt euler

##Verlet

verlet = ones(STEPS + 1 ,3); #format position, time, velocity
f0=(-k*r0-gamma*v0);
verlet(1,1) = r0+dt*v0+(power(dt,2)/2*m)*f0; #r in t-1 using euler's aproximation
verlet(1,2) = -dt; #t-1 
verlet(1,3) = v0+ (dt/m) *f0;#v in t-1 using euler's aproximation

verlet(2,1) = r0;
verlet(2,2) = dt;
verlet(2,3) = v0;
for i = 2:STEPS	#In this case, starting position is r0 and trying to calculate r1, using r(-1)
	previousPos = verlet(i-1,1);
	previousT = verlet(i-1,2);
	previousVelocity = verlet(i-1,3);

	currentPos = verlet(i,1);
	currentT = verlet(i,2);
	currentVelocity = verlet(i,3);

	f = -k * currentPos - gamma * currentVelocity;
	nextPos = 2* currentPos - previousPos + (power(dt,2)/m)* f;
	nextVelocity = (nextPos - currentPos)/dt;#using wikipedia's verlet integration article's aproximation.
	nextT = currentT+ dt;

	verlet(i+1,1)= nextPos;
	verlet(i+1,2) = nextT;
	verlet(i+1,3) = nextVelocity;
end

verlet # to print

save dampingVerlet.txt verlet