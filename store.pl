% Item em inventário
item(1, 'Potion of Healing', 'potions', 10.0, 50).
item(2, 'Wand of Fireball', 'wands', 20.0, 30).
item(3, 'Enchanted Spellbook', 'enchanted_books', 30.0, 20).
item(4, 'Crystal of Clairvoyance', 'crystals', 15.00, 20).
item(5, 'Amulet of Protection', 'amulets', 25.0, 25).
item(6, 'Standard Wand', 'wands', 20.00, 90).
item(7, 'Love Potion', 'potions', 10.0, 50).
item(8, 'Advanced Spellbook', 'enchanted_books', 15.00, 20).
item(9, 'Crystal of Magic Vision', 'crystals', 30.0, 20).
item(10, 'Flying Broomstick', 'accessories', 50.0, 10).
item(11, 'Enchanted Scroll', 'scrolls', 8.00, 45).
item(12, 'Fairy Dust', 'ingredients', 5.0, 100).

% Descontos por categoria de item
discount('potions', 0.1).
discount('wands', 0.2).
discount('enchanted_books', 0.3).
discount('crystals', 0.15).
discount('amulets', 0.25).
discount('accessories', 0.0).
discount('scrolls', 0.2).
discount('ingredients', 0.05).

% Desconto de lealdade por ano
loyalty_discount(1, 0.05).
loyalty_discount(2, 0.1).
loyalty_discount(3, 0.15).
loyalty_discount(4, 0.2).
loyalty_discount(5, 0.25).
loyalty_discount(>5, 0.3).

% Custos de envio por distrito
shipping_cost('Aveiro', 5.0).
shipping_cost('Lisboa', 7.0).
shipping_cost('Porto', 10.0).
shipping_cost('Braga', 2.5).
shipping_cost('Coimbra', 5.0).
shipping_cost('Faro', 15.0).
shipping_cost('Viseu', 3.0).


% Clientes
customer(1, 'Alice', 'Aveiro', 3).
customer(2, 'Beatriz', 'Braga', 1).
customer(3, 'Carlos', 'Coimbra', 2).
customer(4, 'Diogo', 'Lisboa', 4).
customer(5, 'Eva', 'Porto', 1).
customer(6, 'Francisca', 'Faro', 3).
customer(7, 'Guilhermina', 'Viseu', 5).
customer(8, 'Tobias', 'Aveiro', 4).

% Obtém todas as vendas para uma data específica
sales_by_date(Date, SalesString) :-
    findall(Sale,
            (purchase_history(ID, Date, Value, CatDiscount, LoyaltyDiscount, ShippingCost, FinalValue),
             format(atom(Sale), "(Client ID: ~w, Data: '~w', Valor Antes de Desconto: ~2f, Desconto Categoria: ~2f, Desconto Lealdade: ~2f, Custo de Envio: ~2f, Preço Final: ~2f)",
               [ID, Date, Value, CatDiscount, LoyaltyDiscount, ShippingCost, FinalValue])),
            Sales),
    format_sales(Sales, SalesString).

% Formata as transações
format_sales([], "").
format_sales([H|T], SalesString) :-
    format_sales(T, RestSalesString),
    string_concat(H, "\n", HWithNewline),
    string_concat(HWithNewline, RestSalesString, SalesString).

% Calcula os totais para determinada data
sales_totals_by_date(Date, TotalsString) :-
    findall(Value, purchase_history(_, Date, Value, _, _, _, _), Values),
    findall(CatDiscount, purchase_history(_, Date, _, CatDiscount, _, _, _), CatDiscounts),
    findall(LoyaltyDiscount, purchase_history(_, Date, _, _, LoyaltyDiscount, _, _), LoyaltyDiscounts),
    findall(ShippingCost, purchase_history(_, Date, _, _, _, ShippingCost, _), ShippingCosts),
    findall(FinalValue, purchase_history(_, Date, _, _, _, _, FinalValue), FinalValues),
    sum_list(Values, TotalValue),
    sum_list(CatDiscounts, TotalCatDiscount),
    sum_list(LoyaltyDiscounts, TotalLoyaltyDiscount),
    sum_list(ShippingCosts, TotalShippingCost),
    sum_list(FinalValues, TotalFinalValue),
    format(atom(TotalsString), "Total Value: ~2f\nTotal Category Discount: ~2f\nTotal Loyalty Discount: ~2f\nTotal Shipping Cost: ~2f\nTotal Final Value: ~2f",
           [TotalValue, TotalCatDiscount, TotalLoyaltyDiscount, TotalShippingCost, TotalFinalValue]).



% Obtém todas as vendas feitas a determinado cliente
sales_by_client(ClientID, SalesString) :-
    findall(Sale,
            (purchase_history(ClientID, Date, Value, CatDiscount, LoyaltyDiscount, ShippingCost, FinalValue),
             format(atom(Sale), "(Client ID: ~w, Data: '~w', Valor Antes de Desconto: ~2f, Desconto Categoria: ~2f, Desconto Lealdade: ~2f, Custo de Envio: ~2f, Preço Final: ~2f)",
               [ClientID, Date, Value, CatDiscount, LoyaltyDiscount, ShippingCost, FinalValue])),
            Sales),
    format_sales(Sales, SalesString).


% Obtém todas as vendas feitas em determinado distrito e formata
sales_by_district(District, SalesString) :-
    findall(Sale,
            (customer(ClientID, _, District, _),
             purchase_history(ClientID, Date, Value, CatDiscount, LoyaltyDiscount, ShippingCost, FinalValue),
             format(atom(Sale), "(Client ID: ~w, Data: '~w', Valor Antes de Desconto: ~2f, Desconto Categoria: ~2f, Desconto Lealdade: ~2f, Custo de Envio: ~2f, Preço Final: ~2f)",
               [ClientID, Date, Value, CatDiscount, LoyaltyDiscount, ShippingCost, FinalValue])),
            Sales),
    format_sales(Sales, SalesString).

% Calcula o total de vendas para cada distrito
sales_totals_by_district(District, TotalsString) :-
    findall(Value, (customer(ClientID, _, District, _), purchase_history(ClientID, _, Value, _, _, _, _)), Values),
    findall(CatDiscount, (customer(ClientID, _, District, _), purchase_history(ClientID, _, _, CatDiscount, _, _, _)), CatDiscounts),
    findall(LoyaltyDiscount, (customer(ClientID, _, District, _), purchase_history(ClientID, _, _, _, LoyaltyDiscount, _, _)), LoyaltyDiscounts),
    findall(ShippingCost, (customer(ClientID, _, District, _), purchase_history(ClientID, _, _, _, _, ShippingCost, _)), ShippingCosts),
    findall(FinalValue, (customer(ClientID, _, District, _), purchase_history(ClientID, _, _, _, _, _, FinalValue)), FinalValues),
    sum_list(Values, TotalValue),
    sum_list(CatDiscounts, TotalCatDiscount),
    sum_list(LoyaltyDiscounts, TotalLoyaltyDiscount),
    sum_list(ShippingCosts, TotalShippingCost),
    sum_list(FinalValues, TotalFinalValue),
    format(atom(TotalsString), "Total Value: ~2f\nTotal Category Discount: ~2f\nTotal Loyalty Discount: ~2f\nTotal Shipping Cost: ~2f\nTotal Final Value: ~2f",
           [TotalValue, TotalCatDiscount, TotalLoyaltyDiscount, TotalShippingCost, TotalFinalValue]).


% Obtém os distritos ordenados por volume de descontos (categoria + lealdade)
districts_by_discounts(DiscountsListString) :-
    findall(District-TotalDiscount,
            (customer(ClientID, _, District, _),
             findall(CatDiscount, purchase_history(ClientID, _, _, CatDiscount, _, _, _), CatDiscounts),
             findall(LoyaltyDiscount, purchase_history(ClientID, _, _, _, LoyaltyDiscount, _, _), LoyaltyDiscounts),
             sum_list(CatDiscounts, TotalCatDiscount),
             sum_list(LoyaltyDiscounts, TotalLoyaltyDiscount),
             TotalDiscount is TotalCatDiscount + TotalLoyaltyDiscount),
            DistrictsWithDuplicates),
    aggregate_district_discounts(DistrictsWithDuplicates, AggregatedDistricts),
    sort_by_total_discount(AggregatedDistricts, SortedDistrictsWithDiscounts),
    format_discounts(SortedDistrictsWithDiscounts, DiscountsListString).

% Agrega os descontos por distrito
aggregate_district_discounts(DistrictsWithDuplicates, AggregatedDistricts) :-
    aggregate_all(set(District-TotalDiscount),
                  (member(District-_TotalDiscount, DistrictsWithDuplicates),
                   findall(Discount, member(District-Discount, DistrictsWithDuplicates), Discounts),
                   sum_list(Discounts, TotalDiscount)),
                  AggregatedDistricts).

% Ordena o desconto total (ordem decrescente)
sort_by_total_discount(AggregatedDistricts, SortedDistrictsWithDiscounts) :-
    map_list_to_pairs(arg(2), AggregatedDistricts, Pairs),
    keysort(Pairs, SortedPairs),
    reverse(SortedPairs, DescendingSortedPairs),
    pairs_values(DescendingSortedPairs, SortedDistrictsWithDiscounts).

format_discounts([], "").
format_discounts([District-TotalDiscount | T], DiscountsListString) :-
    format_discounts(T, RestDiscountsString),
    format(atom(CurrentDiscountString), "District: ~w, Total Discount: ~2f\n", [District, TotalDiscount]),
    string_concat(CurrentDiscountString, RestDiscountsString, DiscountsListString).


% Obtém os itens por categoria
getItemsByCategory(Category, ItemsString) :-
    findall(ItemString,
            (item(ID, Name, Category, Price, Quantity),
             format(atom(ItemString), "Item ID: ~w, Name: ~w, Price: ~2f, Quantity: ~w", [ID, Name, Price, Quantity])),
            Items),
    atomic_list_concat(Items, '\n', ItemsString).

% Obtém todos os items
getAllItems(ItemsString) :-
    findall(ItemString,
            (item(ID, Name, Category, Price, Quantity),
             format(atom(ItemString), "Item ID: ~w, Name: ~w, Category: ~w, Price: ~2f, Quantity: ~w", [ID, Name, Category, Price, Quantity])),
            Items),
    atomic_list_concat(Items, '\n', ItemsString).


% Obtém todos os custos de envio
getShippingCosts(CostsString) :-
    findall(Cost,
            (shipping_cost(District, CostValue),
             format(atom(Cost), "District: ~w, Cost: ~2f", [District, CostValue])),
            Costs),
    atomic_list_concat(Costs, '\n', CostsString).

% Obtém todos os descontos de categoria
getCategoryDiscounts(DiscountsString) :-
    findall(Discount,
            (discount(Category, DiscountValue),
             format(atom(Discount), "Category: ~w, Discount: ~2f", [Category, DiscountValue])),
            Discounts),
    atomic_list_concat(Discounts, '\n', DiscountsString).

% Obtém todos os descontos de lealdade
getLoyaltyDiscounts(DiscountsString) :-
    findall(Discount,
            (loyalty_discount(Years, DiscountValue),
             format(atom(Discount), "Years: ~w, Discount: ~2f", [Years, DiscountValue])),
            Discounts),
    atomic_list_concat(Discounts, '\n', DiscountsString).


% Obtém dados de todos os clientes
getAllCustomers(CustomersString) :-
    findall(CustomerString,
            (customer(ID, Name, District, LoyaltyYears),
             format(atom(CustomerString), "Customer ID: ~w, Name: ~w, District: ~w, Years of Loyalty: ~w", [ID, Name, District, LoyaltyYears])),
            Customers),
    atomic_list_concat(Customers, '\n', CustomersString).


% Obtém todos os distritos
getAllDistricts(DistrictsString) :-
    findall(District,
            customer(_, _, District, _),
            DistrictsWithDuplicates),
    sort(DistrictsWithDuplicates, Districts),
    atomic_list_concat(Districts, '\n', DistrictsString).

% Obtém todos os clientes de determinado distrito
getCustomersByDistrict(District, CustomersString) :-
    findall(CustomerString,
            (customer(ID, Name, District, LoyaltyYears),
             format(atom(CustomerString), "Customer ID: ~w, Name: ~w, District: ~w, Years of Loyalty: ~w", [ID, Name, District, LoyaltyYears])),
            Customers),
    atomic_list_concat(Customers, '\n', CustomersString).


% Obtém diferentes descontos de lealdade existentes
getAllLoyaltyYears(YearsString) :-
    findall(Years,
            customer(_, _, _, Years),
            YearsWithDuplicates),
    sort(YearsWithDuplicates, Years),
    atomic_list_concat(Years, '\n', YearsString).

% Obtém clientes com determinados anos de lealdade
getCustomersByLoyalty(Years, CustomersString) :-
    findall(CustomerString,
            (customer(ID, Name, District, Years),
             format(atom(CustomerString), "Customer ID: ~w, Name: ~w, District: ~w, Years of Loyalty: ~w", [ID, Name, District, Years])),
            Customers),
    atomic_list_concat(Customers, '\n', CustomersString).



% Histórico de compras
purchase_history(1, '20/05/2024', 50, 5, 0, 5, 50).
purchase_history(2, '21/05/2024', 30, 3, 1, 3, 29).
purchase_history(3, '22/05/2024', 40, 4, 0, 4, 40).
purchase_history(4, '23/05/2024', 60, 6, 2.5, 6, 57.5).
purchase_history(5, '23/05/2024', 25, 2.5, 0, 2.5, 25).
purchase_history(6, '25/05/2024', 35, 3.5, 2, 3.5, 33).
purchase_history(7, '26/05/2024', 75, 7.5, 0, 7.5, 75).
purchase_history(3, '27/05/2024', 45, 4.5, 0, 4.5, 45).
purchase_history(4, '28/05/2024', 55, 5.5, 10, 5, 44.5).
purchase_history(1, '28/05/2024', 60, 6, 0, 6, 60).
purchase_history(5, '17/05/2024', 60.00, 18.00, 3.00, 10.00, 49.00).
purchase_history(3, '17/05/2024', 200.00, 40.00, 20.00, 5.00, 145.00).
purchase_history(3, '17/05/2024', 75.00, 22.50, 7.50, 5.00, 50.00).
purchase_history(2, '17/05/2024', 60.00, 18.00, 3.00, 2.50, 41.50).
purchase_history(3, '17/05/2024', 45.00, 13.50, 4.50, 5.00, 32.00).
purchase_history(4, '17/05/2024', 30.00, 9.00, 6.00, 7.00, 22.00).
purchase_history(2, '17/05/2024', 75.00, 22.50, 3.75, 2.50, 51.25).
purchase_history(6, '17/05/2024', 75.00, 22.50, 11.25, 15.00, 56.25).
purchase_history(8, '17/05/2024', 40.00, 8.00, 8.00, 5.00, 29.00).
purchase_history(8, '17/05/2024', 150.00, 22.50, 30.00, 5.00, 102.50).
purchase_history(8, '17/05/2024', 75.00, 11.25, 15.00, 5.00, 53.75).
purchase_history(2, '17/05/2024', 75.00, 11.25, 3.75, 2.50, 62.50).
purchase_history(8, '17/05/2024', 200.00, 40.00, 40.00, 5.00, 125.00).

