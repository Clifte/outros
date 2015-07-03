close all;
%Modulçao AM
T = 3;        %Tamanho da mensagem em segundos
fs = 1E6;     %SampleRate 

%Calculado amostras para 5 segundos de mensagem
L = fs * T;


%Modulando em AM
alpha = 0.5;     %fator linear normalizador da mensagem
fc = 3E4;   %Frequencia da portadora
A = 1;       %amplitude


%Gerando mensagem
x = linspace(0,1,L);
m = exp(-x) + x.*x + sin(2*pi*x*T) - 2*x ;
%m = sin(2 * pi * x * T * 0.5 )+1.2
subplot(3,1,1);
plot(m);



%modulando AM
mNorm = A * [1 + alpha * m];
y = mNorm .* cos(fc * 2 * pi * x * T);
subplot(3,1,2);
plot(y);hold on;
plot(mNorm,'r');plot(-mNorm,'r'); %Envoltória

%Analisando espectro
NFFT=10000;
Y = fftshift(fft(y,NFFT));	 	 
fVals = fs * (-NFFT/2:NFFT/2-1) / NFFT;	 	 

subplot(3,1,3);
plot(fVals,log(abs(Y)),'b');	

