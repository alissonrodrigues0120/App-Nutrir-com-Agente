package com.example.nutriragente.data.repository

import com.example.nutriragente.R
import com.example.nutriragente.data.model.FormType
import com.example.nutriragente.data.model.OrientacaoTopic

/**
 * Conteúdo educativo exibido na tela de Orientações, organizado por faixa etária
 * (mesmo agrupamento usado nos formulários de consumo alimentar - [FormType]).
 *
 * Baseado nas diretrizes do Ministério da Saúde para alimentação infantil
 * (Guia Alimentar para Crianças Brasileiras Menores de 2 Anos, 2019, e
 * Caderneta de Saúde da Criança). O texto foi resumido e adaptado para
 * consulta rápida pelo Agente Comunitário de Saúde (ACS) durante a visita.
 */
object OrientacoesContentProvider {

    fun subtituloPara(formType: FormType): String = when (formType) {
        FormType.UNDER_6M -> "0 a 6 meses"
        FormType.SIX_TO_23M -> "6 a 23 meses"
        FormType.TWO_YEARS_PLUS -> "2 anos ou mais"
    }

    fun topicosPara(formType: FormType): List<OrientacaoTopic> = when (formType) {
        FormType.UNDER_6M -> topicosAleitamento
        FormType.SIX_TO_23M -> topicosAlimentacaoComplementar
        FormType.TWO_YEARS_PLUS -> topicosPrimeiraInfancia
    }

    // ─────────────────────────────────────────────────────────────────
    // 0 a 6 meses — aleitamento materno exclusivo
    // ─────────────────────────────────────────────────────────────────
    private val topicosAleitamento = listOf(
        OrientacaoTopic(
            title = "Benefícios para a criança",
            iconRes = R.drawable.enfermeira_1,
            bullets = listOf(
                "Fornece todos os nutrientes, água e energia necessários até os 6 meses, sem precisar de mais nada.",
                "Fortalece o sistema imunológico e reduz o risco de diarreia, infecções respiratórias e alergias.",
                "Favorece o vínculo afetivo entre mãe e bebê.",
                "Contribui para o desenvolvimento cognitivo e para a formação adequada da face e da fala.",
                "Reduz o risco de obesidade e de doenças crônicas na vida adulta."
            )
        ),
        OrientacaoTopic(
            title = "Benefícios para a mãe",
            iconRes = R.drawable.mae,
            bullets = listOf(
                "Ajuda o útero a voltar ao tamanho normal mais rápido e reduz o sangramento após o parto.",
                "Nos primeiros 6 meses, em amamentação exclusiva e sem menstruação, pode funcionar como método contraceptivo natural (LAM).",
                "Reduz o risco de câncer de mama e de ovário.",
                "É prático, econômico e está sempre disponível, na temperatura certa.",
                "Fortalece o vínculo mãe-bebê."
            )
        ),
        OrientacaoTopic(
            title = "Técnica inadequada",
            iconRes = R.drawable.mae_com_dor,
            bullets = listOf(
                "Dor ao amamentar e mamilos machucados, rachados ou sangrando são sinais de alerta.",
                "Ruído de \"clique\" durante a mamada costuma indicar má pega.",
                "Bochechas do bebê encovando durante a sucção.",
                "Pega apenas no bico do peito, sem abocanhar boa parte da aréola.",
                "Mama muito cheia e endurecida também dificulta a pega: orientar a mãe a retirar manualmente um pouco de leite antes da mamada, o que ajuda o bebê a pegar melhor.",
                "Se identificar esses sinais, oriente a mãe e, se necessário, encaminhe para avaliação na unidade de saúde."
            )
        ),
        OrientacaoTopic(
            title = "Técnica de amamentação",
            iconRes = R.drawable.mae_forma_c,
            bullets = listOf(
                "A OMS destaca quatro pontos-chave para o posicionamento adequado: rosto do bebê de frente para a mama (nariz na altura do mamilo), corpo próximo ao da mãe, cabeça e tronco alinhados (pescoço não torcido) e bebê bem apoiado.",
                "Pontos-chave da pega adequada: mais aréola visível acima da boca do bebê do que abaixo, boca bem aberta, lábio inferior virado para fora e queixo tocando a mama.",
                "Corpo do bebê bem alinhado e voltado para o corpo da mãe, barriga com barriga.",
                "A mãe deve escolher uma posição adequada e confortável para amamentar, e pode apoiar a mama com a mão em forma de \"C\", deixando a aréola livre, para facilitar a pega."
            )
        ),
        OrientacaoTopic(
            title = "Aspecto do leite",
            iconRes = R.drawable.mae_e_bebe,
            bullets = listOf(
                "Colostro (primeiros dias): mais amarelado e grosso, rico em anticorpos.",
                "O aspecto do leite também varia ao longo de uma mesma mamada: no início, pelo alto teor de água, é parecido com água de coco, mas já bem rico em anticorpos.",
                "No meio da mamada, fica com coloração branca opaca por causa da caseína, proteína que ajuda na sensação de saciedade.",
                "No final (leite posterior), fica mais amarelado e rico em gordura — por isso é importante deixar o bebê esvaziar bem uma mama antes de oferecer a outra.",
                "É normal o leite ter aspecto azulado ou esverdeado quando a mãe consome bastante vegetais verdes; isso não interfere na qualidade do leite.",
                "Não existe \"leite fraco\": toda mulher produz o leite adequado às necessidades do seu bebê."
            )
        ),
        OrientacaoTopic(
            title = "Número de mamadas ao dia",
            iconRes = R.drawable.enfermeira_1_1,
            bullets = listOf(
                "É recomendado amamentar em livre demanda, sem restrição de horário ou tempo de permanência na mama.",
                "Em livre demanda, recém-nascidos costumam mamar entre 8 e 12 vezes em 24 horas, incluindo à noite.",
                "Nos primeiros meses, o estômago do bebê é bem pequeno e o leite materno é digerido em poucas horas, o que justifica mamadas mais frequentes: com 1 dia de vida, a capacidade gástrica é do tamanho de uma cereja (5 a 7 ml); com 3 dias, de uma noz (22 a 27 ml); com 7 dias, de um pêssego (46 a 60 ml); e com 30 dias, de um ovo de galinha (80 a 150 ml).",
                "Não é necessário seguir horários fixos: o ideal é observar os sinais de fome do bebê.",
                "É normal a frequência das mamadas diminuir aos poucos conforme a criança cresce.",
                "Mamadas muito espaçadas ou muito curtas podem indicar dificuldade na amamentação e merecem atenção."
            )
        ),
        OrientacaoTopic(
            title = "Água, chá e outros líquidos",
            iconRes = R.drawable.copo_de_agua,
            bullets = listOf(
                "Não são necessários para bebês em aleitamento materno exclusivo até os 6 meses — o leite materno já contém a água necessária.",
                "Oferecer água, chás, outros leites ou outros líquidos antes dos 6 meses pode reduzir a quantidade de leite materno ingerida, e há evidências de associação com desmame precoce e aumento da morbimortalidade infantil.",
                "Também pode aumentar o risco de infecções, já que esses líquidos nem sempre são preparados com a higiene adequada.",
                "A chupeta também é desaconselhada nesse período, pois pode interferir negativamente na duração do aleitamento materno, entre outros malefícios.",
                "Após os 6 meses, com o início da alimentação complementar, a água passa a ser oferecida à criança (tratada, filtrada ou fervida)."
            )
        ),
        OrientacaoTopic(
            title = "Alimentação da mãe",
            iconRes = R.drawable.mae,
            bullets = listOf(
                "Para a produção de leite é necessária ingestão de calorias e de líquidos além do habitual.",
                "A mãe deve beber no mínimo 1 litro de água filtrada e fervida a mais, além da sua ingestão normal diária.",
                "Mulheres que amamentam não precisam evitar alimentos específicos; se perceberem algum efeito na criança, podem retirar o alimento da dieta por um tempo e reintroduzi-lo depois, observando a reação.",
                "A base da alimentação deve ser de alimentos saudáveis, evitando óleos, gorduras, sal e açúcar em excesso.",
                "Alimentos ultraprocessados (biscoitos recheados, salgadinhos de pacote, refrigerantes, macarrão instantâneo) devem ser evitados também pela mãe que amamenta."
            )
        )
    )

    // ─────────────────────────────────────────────────────────────────
    // 6 a 23 meses — alimentação complementar
    // ─────────────────────────────────────────────────────────────────
    private val topicosAlimentacaoComplementar = listOf(
        OrientacaoTopic(
            title = "Alimentação Complementar",
            iconRes = R.drawable.bebe_com_fruta_inteira,
            bullets = listOf(
                "A partir dos 6 meses o leite materno já não é suficiente sozinho: é hora de introduzir novos alimentos.",
                "Manter a amamentação junto com a alimentação complementar até os 2 anos ou mais.",
                "Introduzir os alimentos de forma lenta e gradual, começando por papas e purês.",
                "É normal a criança rejeitar um alimento novo nas primeiras vezes — oferecer novamente em outras ocasiões, sem forçar.",
                "Não é necessário adicionar sal, açúcar ou temperos industrializados à comida da criança."
            )
        ),
        OrientacaoTopic(
            title = "Esquema alimentar",
            iconRes = R.drawable.bebe_comendo,
            bullets = listOf(
                "Por volta dos 6 meses: leite materno + 1 a 2 papas de fruta e 1 papa salgada por dia. No almoço, a quantidade é de cerca de 2 a 3 colheres de sopa no total, sem seguir isso de forma rígida.",
                "\"Papa de fruta\" indica a consistência (amassada ou raspada); \"papa salgada\" indica um alimento de cada grupo (cereais, feijões, legumes/verduras, carnes/ovos), e não que deve levar sal.",
                "Dos 7 aos 8 meses: 2 papas salgadas e frutas ao longo do dia, mantendo o leite materno, com cerca de 3 a 4 colheres de sopa no almoço/jantar.",
                "Dos 9 aos 11 meses: cerca de 4 a 5 colheres de sopa no almoço/jantar; a alimentação vai se aproximando da comida da família (ainda amassada ou picada), e as carnes já podem ser desfiadas.",
                "Aos 12 meses: a criança já pode comer, com adaptações, a mesma comida da família, em cerca de 5 a 6 refeições por dia.",
                "Sucos naturais não devem substituir uma refeição ou lanche (têm menor densidade energética que a fruta em pedaços); se oferecidos, deve ser em pequena quantidade, após as refeições principais, o que ajuda a absorver melhor o ferro."
            )
        ),
        OrientacaoTopic(
            title = "Grupos de alimentos",
            iconRes = R.drawable.bebe_com_fruta,
            bullets = listOf(
                "Cereais e tubérculos (arroz, batata, inhame, macarrão): fornecem energia.",
                "Leguminosas (feijão, lentilha): fonte de proteína e ferro.",
                "Carnes, vísceras e ovos: ricos em ferro e proteína de alta qualidade.",
                "Frutas, verduras e legumes: fontes de vitaminas, minerais e fibras — variar as cores no prato.",
                "Procurar oferecer alimentos de todos esses grupos ao longo do dia."
            )
        ),
        OrientacaoTopic(
            title = "Consistência do alimento",
            iconRes = R.drawable.pure_batata,
            bullets = listOf(
                "Iniciar com alimentos amassados/em papa — não usar liquidificador ou peneira.",
                "Evoluir progressivamente para alimentos picados ou amassados com o garfo, conforme a criança se desenvolve.",
                "Por volta dos 8-9 meses, oferecer pedaços pequenos e macios para estimular a mastigação.",
                "A comida não deve ser liquidificada, mesmo que a criança ainda tenha poucos dentes."
            )
        ),
        OrientacaoTopic(
            title = "Diversidade de alimentos",
            iconRes = R.drawable.bebe_fruta_icone,
            bullets = listOf(
                "Oferecer variedade de alimentos ao longo da semana, alternando frutas, verduras e fontes de proteína.",
                "Introduzir um alimento novo de cada vez, para observar reações e facilitar a aceitação.",
                "Uma alimentação variada ajuda a garantir a quantidade adequada de ferro, vitaminas e outros nutrientes.",
                "Evitar oferecer sempre os mesmos alimentos."
            )
        ),
        OrientacaoTopic(
            title = "Horários, gratificações e castigos",
            iconRes = R.drawable.ic_calendar,
            bullets = listOf(
                "Estabelecer horários regulares para as refeições, evitando beliscar entre elas.",
                "Não usar comida como prêmio, castigo ou consolo emocional.",
                "Não forçar a criança a comer; respeitar o apetite de cada momento.",
                "Evitar distrações como televisão, celular ou tablet durante as refeições."
            )
        ),
        OrientacaoTopic(
            title = "Sinais de fome e saciedade",
            iconRes = R.drawable.bebe_se_negando,
            bullets = listOf(
                "Fome: a criança chora e se inclina para frente quando a colher se aproxima, segura a mão de quem a alimenta e abre a boca, pega ou aponta para a comida.",
                "Saciedade: vira o rosto ou não quer mais abrir a boca, come mais devagar, fecha a boca e empurra o alimento, ou fica com a comida parada na boca sem engolir.",
                "É importante diferenciar os sinais de fome de outros desconfortos, como sede, sono, frio, calor ou fralda suja — nem todo choro é fome.",
                "Respeitar esses sinais ajuda a criança a desenvolver uma relação saudável com a comida.",
                "É normal a criança comer quantidades variáveis em dias diferentes."
            )
        ),
        OrientacaoTopic(
            title = "Alimentos que devem ser evitados",
            iconRes = R.drawable.sorvete,
            bullets = listOf(
                "Açúcar e alimentos açucarados: evitar antes dos 2 anos.",
                "Sal em excesso.",
                "Alimentos ultraprocessados: refrigerantes, salgadinhos, embutidos, macarrão instantâneo, biscoitos recheados.",
                "Mel antes de 1 ano (risco de botulismo infantil).",
                "Alimentos que podem causar engasgo: uvas inteiras, pipoca, amendoim e outras castanhas inteiras."
            )
        )
    )

    // ─────────────────────────────────────────────────────────────────
    // 2 anos ou mais — primeira infância
    // ─────────────────────────────────────────────────────────────────
    private val topicosPrimeiraInfancia = listOf(
        OrientacaoTopic(
            title = "Esquema alimentar",
            iconRes = R.drawable.bebe_andando,
            bullets = listOf(
                "A criança já participa das refeições da família, com a mesma comida, em pedaços maiores, na mesma consistência da comida da família.",
                "Recomenda-se 3 refeições principais (café da manhã, almoço e jantar) e 2 lanches (frutas ou cereais/tubérculos) por dia.",
                "Café da manhã e lanches: fruta e leite materno, ou cereal (pão, aveia, cuscuz de milho) e leite materno, ou raízes e tubérculos (aipim, batata-doce).",
                "Almoço e jantar: cerca de 5 a 6 colheres de sopa no total, com 1 alimento do grupo dos cereais/tubérculos, 1 do grupo dos feijões, 1 ou mais de legumes/verduras e 1 do grupo das carnes/ovos — sem seguir isso de forma rígida.",
                "Antes de dormir: leite materno.",
                "As refeições principais não devem ser substituídas por lanches ou refeições lácteas, e a criança deve ser encorajada a comer junto com a família, o que estimula autonomia e aceitação dos mesmos alimentos.",
                "Priorizar alimentos in natura e minimamente processados; fazer as refeições em família, em ambiente tranquilo e sem telas."
            )
        ),
        OrientacaoTopic(
            title = "Alimentos que devem ser evitados",
            iconRes = R.drawable.sorvete,
            bullets = listOf(
                "Bebidas açucaradas, sucos industrializados, refrigerantes, frituras, enlatados e embutidos, que têm excesso de sal, aditivos e conservantes artificiais.",
                "Alimentos ultraprocessados em geral, com pouco valor nutricional — associados ao surgimento de anemia, excesso de peso e alergias alimentares por competirem com os alimentos nutritivos e tirarem o apetite da criança.",
                "Se a criança beber líquidos adoçados ou comer guloseimas perto das refeições, pode não ter apetite para o almoço ou jantar que se aproxima.",
                "Pipoca: até os 2 anos, a criança pode ter dificuldade para mastigá-la com segurança, com risco de engasgo e sufocamento.",
                "Macarrão instantâneo: é frito na fabricação, o que resulta em excesso de gordura, calorias, sódio e outros aditivos.",
                "Atenção a outros alimentos que podem causar engasgo (risco que persiste até por volta dos 4 anos): balas duras, uvas inteiras e castanhas inteiras."
            )
        ),
        OrientacaoTopic(
            title = "Deficiência de Ferro e Vitaminas",
            iconRes = R.drawable.vitamina_a,
            bullets = listOf(
                "A falta de ferro (anemia) pode provocar cansaço, fraqueza e falta de apetite; todas as crianças de 6 a 24 meses devem receber fontes extras de ferro de forma preventiva.",
                "A deficiência de vitamina A pode causar problemas graves de visão, além de aumentar o risco de diarreia e infecções respiratórias; crianças de 6 meses a 5 anos em área de risco devem ser suplementadas.",
                "Suplementação de vitamina A: 1 megadose de 100.000 UI para crianças de 6 a 11 meses; 1 megadose de 200.000 UI a cada 6 meses para crianças de 12 a 59 meses.",
                "Suplementação de ferro: para crianças amamentadas exclusivamente, deve começar a partir do 6º mês de vida, conforme prescrição do profissional de saúde, e seguir até os 2 anos de idade.",
                "As suplementações são ofertadas nas Unidades Básicas de Saúde.",
                "Priorizar alimentos ricos em ferro (carnes, vísceras, feijão, vegetais verde-escuros) associados a uma fonte de vitamina C, que ajuda na absorção.",
                "Verifique se a criança está inserida nos programas locais de suplementação de ferro e vitamina A do Ministério da Saúde e, se necessário, encaminhe à unidade de saúde.",
                "Sinais de alerta para investigar: palidez, cansaço frequente, apetite reduzido."
            )
        ),
        OrientacaoTopic(
            title = "Sinais de fome e de saciedade",
            iconRes = R.drawable.bebe_chorando,
            bullets = listOf(
                "Fome: a criança pede comida, aponta ou leva a mão à boca, fica irritada antes das refeições.",
                "Saciedade: desacelera o ritmo, brinca com a comida, recusa novas porções, vira a cabeça.",
                "É comum a recusa de alimentos novos (\"neofobia alimentar\") nessa fase — insistir com calma, sem forçar, oferecendo o mesmo alimento em preparações diferentes.",
                "Deixar a criança participar da refeição (por exemplo, comer sozinha) ajuda a reconhecer seus próprios sinais de fome e saciedade."
            )
        ),
        OrientacaoTopic(
            title = "Calendário de puericultura",
            iconRes = R.drawable.ic_calendar,
            bullets = listOf(
                "A puericultura consiste em um acompanhamento periódico visando a promoção e proteção da saúde das crianças, por meio de um acompanhamento integral em saúde.",
                "O Ministério da Saúde recomenda consultas de rotina na 1ª semana, no 1º, 2º, 4º e 6º mês de vida.",
                "Depois, seguem consultas no 9º e 12º mês, e posteriormente no 18º e 24º mês de vida.",
                "Nessas consultas são avaliados peso, altura, perímetro cefálico, desenvolvimento neuropsicomotor e situação vacinal.",
                "O ACS tem papel importante em lembrar as famílias das datas das consultas e verificar se a caderneta está sendo preenchida corretamente.",
                "Aproveite as visitas domiciliares para reforçar a importância do acompanhamento regular na unidade de saúde."
            )
        ),
        OrientacaoTopic(
            title = "Alimentos ricos em ferro e vitaminas",
            iconRes = R.drawable.bebe_com_fruta,
            bullets = listOf(
                "Alimentos ricos em ferro: carnes de gado, aves (frango, galinha), peixes e fígado.",
                "Alimentos ricos em vitamina A: fígado, gema de ovo, leite de vaca; frutas e legumes amarelo-alaranjados (manga, mamão, caqui, abóbora, cenoura, batata-doce); vegetais folhosos verdes (espinafre, couve, brócolis); e óleos e frutas oleaginosas (buriti, pupunha, dendê, pequi).",
                "Alimentos ricos em vitamina C: laranja, limão, abacaxi, tomate, acerola, goiaba, kiwi e manga.",
                "Os alimentos ricos em vitamina C devem ser oferecidos junto à refeição principal, ou logo após, para aumentar a absorção do ferro."
            )
        )
    )
}