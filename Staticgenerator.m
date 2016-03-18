ammount= 500;
c = {ammount;20}
information = ones(ammount,2);
information(:,1) = 0.25;
c{3} = information;

save static500.txt c;