% set methodology up
clear all;
clc;
close all;
realizations = 30;
train_percs = 80;

% set params up
params.gamma = 0.04;
params.max_sv = 200;

% data loading
ds = DataSet('column_2C.dat');
ds.normalize();

hit_rate = zeros(realizations,1);
sv_rate = zeros(realizations,1);
accCalc = [];
for i = 1 : realizations
    i
    
    [train_features train_labels test_features test_labels] = ds.shuffle(train_percs);

    my = OPLSSVM(train_features, train_labels, params);
    if(length(accCalc))
        accCalc = accCalc + my.train();
    else
        accCalc = my.train();
    end
    
    %my = PrunningLSSVM(train_features, train_labels, params);
    my = MyLSSVM(train_features, train_labels, params);   
    my.train();

    %make decision function
    output_labels = my.classify(test_features);
    hit_rate(i,1) = length(find((output_labels - test_labels) == 0))/size(test_features,1);
    hit_rate(i,1);
    sv_rate(i,1) = length(my.alphas);
end


hit_mean_rate = mean(hit_rate);
hit_mean_rate

hit_dsv_rate = std(hit_rate);
hit_dsv_rate

hit_sv = (sum(sv_rate)/length(sv_rate));
hit_sv


hit_sv_rate =  (sum(sv_rate)/length(sv_rate))/size(train_features,1);
hit_sv_rate




if(length(accCalc))
    accCalc = accCalc / realizations;
    plot([0 params.max_sv ],[hit_mean_rate hit_mean_rate ],'r'); hold on;
    plot(accCalc(2,:),accCalc(1,:));
    title('OPLSSVM')
    xlabel('Quantidade de neurônios');
    ylabel('Acurácia');
    legend(['LSSVM  ';'OPLSSVM']);
    
    [v id] = sort(accCalc(1,1:50),'descend');
    accCalc(2,id(1:5))
end

