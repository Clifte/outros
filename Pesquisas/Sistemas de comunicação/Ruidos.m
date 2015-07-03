close all;



%%cosseno
x = linspace(0, 2 * pi,100);
m = round(interp(rand(10,1),10,1));


frequencias = m * 100E3; %até 100khz
y = cos(frequencias*x*2*pi);

%Gauss / Amplitude
y = m;

%AM
y = 10 .* m' .* cos(50E3 * 2 * pi* x);


plot(y');
hold on;
plot(m,'r')

tmp = sum(y,1);

m1 = sum(tmp(1:25));
m2 = sum(tmp(26:50));
m3 = sum(tmp(51:75));
m4 = sum(tmp(76:100));

M = [m1 m2 m3 m4];

if(std(M) > 3)
    fprintf('Processo nao estacionario %f',std(M));
else
    fprintf('Processo estacionario %f',std(M));
end