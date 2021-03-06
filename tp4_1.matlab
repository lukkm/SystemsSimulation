####### DAMPED OSCILLATOR #######

####### Parameters #######
dt = 0.1/8;
m = 70;
k = power(10,4);
gamma = 100;
tf = 5;

#### initial conditions
r0 = 1;
v0 = - gamma / (m/2);

#######  END Parameters #######

####### Analytic ##########
# Format: position, time ##
###########################
r = 0;
t = 0;
i = 2;
r(1,1) = r0;
r(1,2) = t;
while t < tf
	r(i,1) = exp( - (gamma/(2*m)) * t) * cos( sqrt(k/m - power((gamma/(2*m)),2)) * t);
	r(i,2) = t;
	t = t + dt;
	i++;
end
steps_r = size(r,1);
xlabel("Tiempo");ylabel("Posicion");
plot(r(:,2),r(:,1),'r;Analitico ;')

input("Continue?")
save dampingExact.txt r

################# Euler  #################
# Format: position, time, velocity #######
##########################################
euler = 0;
t = 0;
i = 2;
euler(1,1) = r0;
euler(1,2) = t;
euler(1,3) = v0;
while t < tf
	f = -k * euler(i-1,1) - gamma * euler(i-1,3);
	euler(i,1) = euler(i-1,1) + dt *euler(i-1,3)+ (power(dt,2)/(2*m)) * f;
	euler(i,2) = t + dt	;
	euler(i,3) = euler(i-1,3) + (dt/m) * f;
	t = t + dt;
	i++;
end
steps_eul = size(euler,1);
plot(euler(:,2),euler(:,1),'g;Euler ;')
xlabel("Tiempo");ylabel("Posicion");

plot(r(:,2),r(:,1),'r;Analitico ;',euler(:,2),euler(:,1),'g-;Euler ;');
xlabel("Tiempo");ylabel("Posicion");
sum ( abs(r(:,1)-euler(:,1)).^2) / steps_r

input("Continue?")
save dampingEuler.txt euler(:,1:2)

######### Predictor corrector Euler Modificado  ###############
#######   Format: position, time, velocity       ##############
###############################################################
t = 0;
i = 2;
predictor(1,1) = r0;
predictor(1,2) = t;
predictor(1,3) = v0;
f0= -k * r0 - gamma* v0;
while t < tf
	f = -k * predictor(i-1,1) - gamma * predictor(i-1,3);
	predictor(i,3) =  predictor(i-1,3) + (f/m) *dt;
	predictor(i,1) =  predictor(i-1,1) + predictor(i-1,3)  * dt;

	at_p1 = (-k * predictor(i,1) - gamma * predictor(i,3))/m;

	predictor(i,3) =  predictor(i-1,3) + at_p1 * dt;
	predictor(i,1) =  predictor(i-1,1)  + predictor(i,3)*dt;
	t = t + dt;
	i++;
end
plot(predictor(:,2),predictor(:,1),'k;Predictor-Corrector Euler;')
xlabel("Tiempo");ylabel("Posicion");

plot(r(:,2),r(:,1),'r;Analitico ;',predictor(:,2),predictor(:,1),'k;Predictor-Corrector Euler;')
xlabel("Tiempo");ylabel("Posicion");
sum ( abs(r(:,1)-predictor(:,1)).^2) / steps_r


############## Beeman ####################
# Format: position, time, velocity #######
##########################################
beeman = 0;
t = 0;
i = 2;
f0=(-k*r0-gamma*v0);
beeman(1,1) = r0+dt*v0+(power(dt,2)/(2*m))*f0;
beeman(1,2) = -dt;
beeman(1,3) = v0+ (dt/m) *f0;

beeman(2,1) = r0;
beeman(2,2) = t;
beeman(2,3) = v0;
#starting at t =0, i = 2 ,using a = f/m to calculate at_m1 at at_p1
while t < tf
	at_m1 = (-k* beeman(i - 1 ,1) - gamma * beeman(i - 1 ,3))/m;
	at = (-k* beeman(i ,1) - gamma * beeman(i ,3))/m;
	beeman(i + 1,1) = beeman(i ,1) + beeman(i,3) * dt + (2/3) * dt^2 * at - (1/6)* dt^2 * at_m1 ;
	beeman(i + 1,2) = t + dt;
	beeman(i + 1,3) = (1 + ((dt*gamma)/(3*m)))^-1 * ( beeman(i,3) -((dt*k)/(3*m))*beeman(i+1,3) + (5/6) * at * dt -(1/6) * at_m1 * dt);
	t = t + dt;
	i++;
end
steps_bee = size(beeman,1);
plot(beeman(2:steps_bee,2),beeman(2:steps_bee,1),'c;Beeman ;');
xlabel("Tiempo");ylabel("Posicion");

plot(r(:,2),r(:,1),'r;Analitico ;',beeman(2:steps_bee,2),beeman(2:steps_bee,1),'c;Beeman ;');
xlabel("Tiempo");ylabel("Posicion");

sum ( (r(:,1)-beeman(2:steps_bee,1)).^2) / steps_r

input("Continue?")
save dampingBeeman.txt beeman(:,1:2)


############## Verlet ####################
# Format: position, time, velocity #######
##########################################


###### Step t-1 using euler's approximation
f0=(-k*r0-gamma*v0);
verlet(1,1) = r0+dt*v0+(power(dt,2)/(2*m))*f0;
verlet(1,2) = -dt;
verlet(1,3) = v0+ (dt/m) *f0;

t = 0;
i = 2;
verlet(2,1) = r0;
verlet(2,2) = t;
verlet(2,3) = v0;
##########################################################################################
###### In this case, starting position is r0 and trying to calculate r1, using r(-1)######
##########################################################################################
while t < tf
	previousPos = verlet(i-1,1);
	previousT = verlet(i-1,2);
	previousVelocity = verlet(i-1,3);

	currentPos = verlet(i,1);
	currentT = verlet(i,2);
	currentVelocity = verlet(i,3);

	f = -k * currentPos - gamma * currentVelocity;
	nextPos = 2* currentPos - previousPos + (power(dt,2)/m)* f;
	#### using wikipedia's verlet integration article's aproximation.
	nextVelocity = (nextPos - currentPos)/dt;
	nextT = currentT+ dt;

	verlet(i+1,1)= nextPos;
	verlet(i+1,2) = nextT;
	verlet(i+1,3) = nextVelocity;
	t = t + dt;
	i++;
end
steps_ver = size(verlet,1);
plot(verlet(2:steps_ver,2),verlet(2:steps_ver,1),'m;Verlet ;');

plot(r(:,2),r(:,1),'r;Analitico ;',verlet(2:steps_ver,2),verlet(2:steps_ver,1),'m;Verlet ;');
xlabel("Tiempo");ylabel("Posicion");

sum ( (r(:,1)-verlet(2:steps_ver,1)).^2) / steps_r


input("Finish?")
save dampingVerlet.txt verlet
plot(r(:,2),r(:,1),'r;Analitico ;',euler(:,2),euler(:,1),'g;Euler ;',predictor(:,2),predictor(:,1),'k;Predictor-Corrector Euler;',beeman(2:steps_bee,2),beeman(2:steps_bee,1),'c;Beeman ;',verlet(2:steps_ver,2),verlet(2:steps_ver,1),'m;Verlet ;');
xlabel("Tiempo");ylabel("Posicion");