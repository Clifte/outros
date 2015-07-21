classdef OPLSSVM < handle
    
   properties
      train_features % features of training dataset
      train_labels % labels of training dataset
      gamma % trade-off between hit and error costs
      bias % bias of LSSVM classifier
      alphas % Lagrange multipliers of LSSVM classifier

      
      
      max_sv %Max number of suport vectors
      cadidate_sv %Suport Vectors Indexes
   end
   
   methods
      function obj = OPLSSVM(train_features, train_labels, params)
         obj.train_features = train_features;
         obj.train_labels = train_labels;
         obj.gamma = params.gamma;
         obj.bias = [];
         obj.alphas = [];
         obj.max_sv = params.max_sv;
      end
      
      function [train_accuracy infos] = train(obj)
        % getting A.x = b problem
        A = (obj.train_features*obj.train_features').*(obj.train_labels*obj.train_labels');
        A = A + (1/obj.gamma)*eye(size(obj.train_features,1));
         
        %ranking mrsr
        [W,indexes] = mrsr(obj.train_labels,A,obj.max_sv);       
        obj.cadidate_sv = indexes;
        
        %Preservando variáveis
        Ao = A;
        indexes_o = indexes;
        train_features_o = obj.train_features;
        train_labels_o = obj.train_labels;        
        
        
        %% LOO
        
        n=1;
        nn = obj.max_sv;
        err     = inf(nn,n);
        mycond  = zeros(1,nn);
        errloo  = Inf(nn,n);
        train_accuracy =  zeros(nn,1);
        
        
        step = 2;

        
        
        for i=1:step:length(indexes_o)
             
            indexes = indexes_o(1:i);
            A = Ao(:,indexes);

            %adding upper and left info
            A = [train_labels_o(indexes)'; A];
            A = [[0; train_labels_o] A];
            b = ones(size(A,1),1);
            b(1,1) = 0;

            P = A'*A;
            if (0)
                x = A\b;
            else    
                x = (P)\(A'*b);
            end

            
            
            obj.bias = [x(1,1)];
            obj.alphas = [x(2:size(x,1),1)];

            obj.train_features = train_features_o(indexes,:);
            obj.train_labels = train_labels_o(indexes,:);   
            
            
            
            %Verificando regularização e calculando erro médio quadrático
            mycond(i) = rcond(inv(P));
            if mycond(1,i)>1e-017
                err(i,1:n) = mean((train_labels_o - obj.estimate(train_features_o)).^2);
            else
                err(i,1:n) = inf;
                break
            end
            %Descarta caso a variancia seja maior que o erro
%             if ((i>1) && ((min(errloo(i,:)>var(obj.train_labels)*1.5)) || ((min(errloo(i,:)>min(errloo)*1.5)))))
%                 break
%             end    
                
            train_accuracy(i) = obj.accuracy(train_features_o, train_labels_o);
        end
%%
            %% Plotando gráficos de acuracia e erro médio quadrático
            
             tmp_idx = find(train_accuracy~=0);
%             plot(tmp_idx,train_accuracy(tmp_idx))
%         
%             tmp_idx = find(err~=inf);
%             plot(tmp_idx,err(tmp_idx))       
%         
            infos={};
            infos.err = err;
            infos.train_accuracy = train_accuracy;
            infos.nn = tmp_idx;
            
            %% %ajustando para valores ótimos
            [v id] = max(train_accuracy);
            
            indexes = indexes_o(1:id);
            A = Ao(:,indexes);

            %adding upper and left info
            A = [train_labels_o(indexes)'; A];
            A = [[0; train_labels_o] A];
            b = ones(size(A,1),1);
            b(1,1) = 0;

            P = (A'*A);
            
            if (1)
                x = A\b;
            else    
                x = P \ (A'*b);
            end


            obj.bias = [x(1,1)];
            obj.alphas = [x(2:size(x,1),1)];

            obj.train_features = train_features_o(indexes,:);
            obj.train_labels = train_labels_o(indexes,:);    
            
      end
      
      function result = accuracy(obj,features,labels)
        output_labels = obj.classify(features);
        result = length(find((output_labels - labels) == 0))/size(labels,1);
      end    
      
      
      %Utilizado em problemas de classificação
      function result = classify(obj,test_features)
        %make decision function
        result = sign(sum(test_features * obj.train_features'.* repmat(obj.alphas' .* obj.train_labels',size(test_features,1),1),2) + obj.bias);
      end
      
      %Utilizado em problemas de regresão
      function result = estimate(obj,test_features)
        %make decision function
        result = (sum(test_features * obj.train_features'.* repmat(obj.alphas' .* obj.train_labels',size(test_features,1),1),2) + obj.bias);
      end
      
   end % methods
end % classdef 