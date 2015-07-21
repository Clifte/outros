%Teste RBF

load fisheriris.mat
X = meas;
Y = zeros(length(species),1);


Y(strcmp(species,'setosa')) = 1;
Y(strcmp(species,'versicolor')) = 2;
Y(strcmp(species,'virginica')) = 3;
[m n] = size(X);

nClasses = 3;



%Normalização
mini = min(X);
Xn = X - repmat(mini,[m,1]);
maxi = max(Xn);
Xn = Xn ./  repmat(maxi,[m,1]);


D = pdist(Xn);
D = squareform(D);


for c=1:nClasses
    Yl = ones(m,1);
    Yl (Y~=c) = 0;
    
    nPos = sum(Yl==1);
    
    subMD = D(find(Yl),find(~Yl)) / sqrt(n);
    
    
    subMD = sort(subMD(:));
    N = (m-nPos) * nPos;
    plot(linspace(0,1,N),subMD);
    
end





