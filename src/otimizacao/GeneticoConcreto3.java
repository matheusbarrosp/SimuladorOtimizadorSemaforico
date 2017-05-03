package otimizacao;

import entidades.Cruzamento;
import java.util.ArrayList;
import java.util.Random;
import simulacao.Chegada;
import simulacao.Rede;

public class GeneticoConcreto3 implements Genetico
{

    public static int TAMANHO_POPULACAO = 100;
    public static int TAXA_MUTACAO = 5;
    public ArrayList<Chegada> chegadas;
    public IndividuoGenetico inicial;
    
    public GeneticoConcreto3(ArrayList<Chegada> chegadas)
    {
        this.inicial = null;
        this.chegadas = chegadas;
    }

    @Override
    public Populacao gerarNovaPopulacao(Individuo[][] individuosAux, int max)
    {
        IndividuoGenetico[][] individuos = (IndividuoGenetico[][]) individuosAux;
        Populacao nova = new Populacao(max);
        //System.out.println("NOVA POPULACAO");
        for (int i = 0; i < max; i++)
        {
            //System.out.println("CRUZA PARA GERAR INDIVIDUO " + i);
            nova.addIndividuo(cruzarIndividuos(individuos[i][0], individuos[i][1]));
        }
        return nova;
    }

    @Override
    public IndividuoGenetico cruzarIndividuos(Individuo individuo1, Individuo individuo2)
    {
        IndividuoGenetico i1 = (IndividuoGenetico) individuo1;
        IndividuoGenetico i2 = (IndividuoGenetico) individuo2;
        //System.out.println("---------------------");
        //System.out.println("ESTOU CRUZANDO: ");
        //System.out.println(i1.toString());
        //System.out.println(i2.toString());
        //System.out.println("---------------------");
        int numCruzamentos = i1.getConfiguracoes().size();
        ArrayList<int[]> configuracao = new ArrayList<int[]>();
        int[] atrasos = new int[numCruzamentos];
        Random rand = new Random();
        boolean solucaoEscolhida;
        for (int i = 0; i < numCruzamentos; i++)
        {
            //System.out.println("INDICE: " + i);
            solucaoEscolhida = rand.nextBoolean();
            if (solucaoEscolhida == true)
            {
                //System.out.println("ESCOLHI 1");
                //for (int cont = 0; cont < i1.getConfiguracoes().get(i).length; cont++)
                //{
                //    System.out.print(i1.getConfiguracoes().get(i)[cont] + " ");
                //}
                int[] aux = new int[i1.getConfiguracoes().get(i).length];
                for (int cont = 0; cont < i1.getConfiguracoes().get(i).length; cont++)
                {
                    aux[cont] = i1.getConfiguracoes().get(i)[cont];
                }
                configuracao.add(aux);
                //System.out.println("\nATRASO: " + i1.getAbertura()[i]);
                atrasos[i] = i1.getAbertura()[i];
            } else
            {
                //System.out.println("ESCOLHI 2");
                //for (int cont = 0; cont < i2.getConfiguracoes().get(i).length; cont++)
                //{
                //    System.out.print(i2.getConfiguracoes().get(i)[cont] + " ");
                //}
                int[] aux = new int[i2.getConfiguracoes().get(i).length];
                for (int cont = 0; cont < i2.getConfiguracoes().get(i).length; cont++)
                {
                    aux[cont] = i2.getConfiguracoes().get(i)[cont];
                }
                configuracao.add(aux);
                //System.out.println("\nATRASO: " + i2.getAbertura()[i]);
                atrasos[i] = i2.getAbertura()[i];
            }
        }
        return new IndividuoGenetico(configuracao, atrasos);
    }

    @Override
    public void mutacao(Populacao populacao)
    {
        Random rand = new Random();
        int probabilidade;
        int cont = 0;
        for (Individuo i : populacao.getIndividuos())
        {
            probabilidade = rand.nextInt(100);
            if (probabilidade >= 0 && probabilidade <= TAXA_MUTACAO)
            {
                //System.out.println("MUTEI INDIVIDUO " + cont);
                this.mutar((IndividuoGenetico) i);
            }
            cont++;
        }
    }

    @Override
    public void mutar(Individuo individuo)
    {
        IndividuoGenetico i = (IndividuoGenetico) individuo;
        Random rand = new Random();
        int cruzamentoMutado = rand.nextInt(i.getConfiguracoes().size());
        int tam = i.getConfiguracoes().get(cruzamentoMutado).length;
        i.alteraConfiguracao(cruzamentoMutado, this.geraConfiguracaoAleatoria(tam));
        int somatorio = 0;
        for (int j = 1; j < tam; j++)
        {
            somatorio += i.getConfiguracoes().get(cruzamentoMutado)[j];
        }
        i.getAbertura()[cruzamentoMutado] = rand.nextInt(somatorio + 1);
    }

    private IndividuoGenetico[][] torneio(Populacao populacao)
    {
        Random rand = new Random();
        int i1, i2;
        int max = populacao.getMax();
        IndividuoGenetico[][] vencedores = new IndividuoGenetico[max][2];
        for (int i = 0; i < max; i++)
        {
            i1 = rand.nextInt(max);
            do
            {
                i2 = rand.nextInt(max);
            } while (i1 == i2);
            vencedores[i][0] = this.compara((IndividuoGenetico) populacao.getIndividuo(i1), 
                    (IndividuoGenetico) populacao.getIndividuo(i2));

            i1 = rand.nextInt(max);
            do
            {
                i2 = rand.nextInt(max);
            } while (i1 == i2);
            vencedores[i][1] = this.compara((IndividuoGenetico) populacao.getIndividuo(i1), 
                    (IndividuoGenetico) populacao.getIndividuo(i2));
        }
        return vencedores;
    }

    private IndividuoGenetico compara(IndividuoGenetico i1, IndividuoGenetico i2)
    {
        if (i1.getSolucao() < i2.getSolucao())
        {
            return i1;
        } else
        {
            return i2;
        }
    }

    @Override
    public Populacao geraPopulacaoInicial()
    {
        Rede rede = Rede.getInstance();
        int tam = TAMANHO_POPULACAO;
        Populacao inicial = new Populacao(tam);
        for (int i = 0; i < tam; i++)
        {
            inicial.addIndividuo(criaIndividuo());
        }
        return inicial;
    }
    
    public ArrayList<int[]> clonarConfiguracaoBase()
    {
        ArrayList<int[]> configuracoes = new ArrayList<int[]>();
        ArrayList<int[]> configuracaoBase = this.inicial.getConfiguracoes();
        int i=0;
        for (Cruzamento c : Rede.getInstance().getCruzamentos())
        {
            if (c.getTemSemaforo() == 1)
            {
                int[] configuracao = new int[configuracaoBase.get(i).length];
                for(int j=0; j<configuracaoBase.get(i).length; j++)
                {
                    configuracao[j] = configuracaoBase.get(i)[j];
                }
                configuracoes.add(configuracao);
                i++;
            }
        }
        return configuracoes;
    }
    
    @Override
    public IndividuoGenetico criaIndividuo()
    {
        int numCruzamentosSinalizados = 0;
        for (Cruzamento c : Rede.getInstance().getCruzamentos())
        {
            if (c.getTemSemaforo() == 1)
            {
                numCruzamentosSinalizados++;
            }
        }
        int[] abertura = new int[numCruzamentosSinalizados];
        Random rand = new Random();
        int i = 0;
        ArrayList<int[]> configuracaoBase = this.inicial.getConfiguracoes();
        int[] atrasoBase = this.inicial.getAbertura();
        ArrayList<int[]> configuracoes = this.clonarConfiguracaoBase();
        for (Cruzamento c : Rede.getInstance().getCruzamentos())
        {
            if (c.getTemSemaforo() == 1)
            {
                //System.out.print("\nConfiguracao: ");
                //for (int j = 0; j < configuracao.length; j++) System.out.print(configuracao[j] + " ");
                //System.out.println(" ");
                do
                {
                    abertura[i] = atrasoBase[i] + (10 - rand.nextInt(21));
                }while(abertura[i] < 0);
                //System.out.println("Abertura: " + abertura[i]);
                i++;
            }
        }
        return new IndividuoGenetico(configuracoes, abertura);
    }

    @Override
    public int[] geraConfiguracaoAleatoria(int tam)
    {
        int[] configuracao = new int[tam];
        for (int i = 0; i < tam; i++)
        {
            Random rand = new Random();
            configuracao[i] = rand.nextInt(51) + 10;
        }
        return configuracao;
    }

    @Override
    public IndividuoGenetico encontraMelhorSolucao(Populacao populacao)
    {
        IndividuoGenetico melhor = null;
        for (Individuo i : populacao.getIndividuos())
        {
            if (melhor == null)
            {
                melhor = (IndividuoGenetico) i;
            } else if (melhor.getSolucao() > i.getSolucao())
            {
                melhor = (IndividuoGenetico) i;
            }
        }
        return melhor;
    }

    @Override
    public void simularIndividuos(Populacao populacao)
    {
        int cont = 0;
        for (Individuo individuo : populacao.getIndividuos())
        {
            IndividuoGenetico i = (IndividuoGenetico) individuo;
            //System.out.println("Simulando individuo " + cont);
            i.simular(Rede.getInstance().cloneChegadas(this.chegadas));
            cont++;
        }
    }

    @Override
    public Populacao novaGeracao(Populacao populacao)
    {
        IndividuoGenetico[][] vencedores = this.torneio(populacao);
        Populacao nova = this.gerarNovaPopulacao(vencedores, populacao.getMax());
        this.mutacao(nova);
        return nova;
    }

    @Override
    public IndividuoGenetico otimizar(Individuo inicialAbstrato)
    {
        System.out.println("Iniciando Algoritmo Genetico...");
        this.inicial = (IndividuoGenetico) inicialAbstrato;
        Populacao populacao = this.geraPopulacaoInicial();
        this.simularIndividuos(populacao);
        IndividuoGenetico melhorDaGeracao;
        IndividuoGenetico melhorSolucao;
        melhorSolucao = this.encontraMelhorSolucao(populacao);
        if(melhorSolucao.getSolucao() > inicial.getSolucao()) 
        {
            melhorSolucao = inicial;
        }
        for (int geracao = 0; geracao < 5; geracao++)
        {
            System.out.println("\n\nGeracao " + (geracao + 1) + ": ");
            populacao = this.novaGeracao(populacao);
            this.simularIndividuos(populacao);
            melhorDaGeracao = this.encontraMelhorSolucao(populacao);
            if (melhorSolucao.getSolucao() > melhorDaGeracao.getSolucao())
            {
                melhorSolucao = melhorDaGeracao;
            }
        }
        melhorSolucao.imprimirResultados();
        return melhorSolucao;
    }
}
