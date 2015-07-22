% set methodology up
clear all;
clc;
close all;
addpath('../util/');
addpath('../databases/');
addpath('../lssvm/');


realizations = 5;
train_percs = 80;

% set params up
params.gamma = 0.04;
params.max_sv = 200;



bases = {'colunaVertebral.data','breastCancer.data','dermatology.data','diabetes.data','haberman.data','iris.data'};
figure;
for b=1:length(bases)

    % data loading
    ds = DataSet(cell2mat(bases(b)));
    ds.normalize();

    hit_rate = zeros(realizations,1);
    sv_rate = zeros(realizations,1);

    accCalc = zeros(realizations, params.max_sv);
    err = zeros(realizations, params.max_sv);

    ds.labels(ds.labels~=1)=-1;
    ds.labels(ds.labels==1)=1;

    for i = 1 : realizations
        i
        %holdout padrao
        [train_features train_labels test_features test_labels] = ds.shuffle(train_percs);

        [my] = OPLSSVM(train_features, train_labels, params);

        if(length(accCalc))
            [acc info] = my.train();
            accCalc(i,:) = acc;
            err(i,:) = info.err;
        end

        %my = PrunningLSSVM(train_features, train_labels, params);
        my = MyLSSVM(train_features, train_labels, params);   
        my.train();

        %make decision function
        output_labels = my.classify(test_features);
        hit_rate(i,1) = length(find((output_labels - test_labels) == 0))/size(test_features,1);

        sv_rate(i,1) = length(my.alphas);
    end
    
    stdDvCalc = std(accCalc);
    accCalcM = mean(accCalc); %Media de realizacoes
    
    err = mean(err);
    
    hit_mean_rate = mean(hit_rate);
    hit_mean_rate

    hit_dsv_rate = std(hit_rate);
    hit_dsv_rate

    hit_sv = (sum(sv_rate)/length(sv_rate));
    hit_sv


    hit_sv_rate =  (sum(sv_rate)/length(sv_rate))/size(train_features,1);
    hit_sv_rate




    if(length(accCalcM))
          color = rand(1,3);
%         accCalc = accCalc / realizations;
%         err = err / realizations;
%         subplot(3,1,1);
%         plot([0 params.max_sv ],[hit_mean_rate hit_mean_rate ],'color',color); hold on;
%         title('LSSVM')
%         xlabel('Quantidade de neurônios');
%         ylabel('Acurácia');
        
        [v i] = max(accCalcM(info.nn));
        subplot(2,1,1);
        plot([0 params.max_sv ],[hit_mean_rate hit_mean_rate ],'--','color',color); hold on;
        plot(info.nn,accCalcM(info.nn),'color',color); hold on;
        scatter(info.nn(i),v,'MarkerEdgeColor',color,...
              'MarkerFaceColor',color,...
              'LineWidth',1.5)
        
        title('OPLSSVM')
        xlabel('Quantidade de neuronios');
        ylabel('Acurácia');

        
        subplot(2,1,2); 
        plot(info.nn,err(info.nn),'color',color); hold on;
        
        title('Erro Q medio')
        xlabel('Quantidade de neuronios');
        ylabel('Error');
        
        
        drawnow;
        %[v id] = sort(accCalc(1,1:50),'descend');
        %accCalc(2,id(1:5))
    end
    
    
end

legend(bases)


